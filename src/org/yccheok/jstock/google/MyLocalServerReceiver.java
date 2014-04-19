/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.yccheok.jstock.google;

import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.util.Throwables;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * OAuth 2.0 verification code receiver that runs a Jetty server on a free port, waiting for a
 * redirect with the verification code.
 *
 * <p>
 * Implementation is thread-safe.
 * </p>
 *
 * @since 1.11
 * @author Yaniv Inbar
 */
public final class MyLocalServerReceiver implements VerificationCodeReceiver {

  private static final String CALLBACK_PATH = "/Callback";

  /** Server or {@code null} before {@link #getRedirectUri()}. */
  private Server server;

  /** Verification code or {@code null} for none. */
  String code;

  /** Error code or {@code null} for none. */
  String error;

  /** Lock on the code and error. */
  final Lock lock = new ReentrantLock();

  /** Condition for receiving an authorization response. */
  final Condition gotAuthorizationResponse = lock.newCondition();

  /** Port to use or {@code -1} to select an unused port in {@link #getRedirectUri()}. */
  private int port;

  /** Host name to use. */
  private final String host;

  /**
   * Constructor that starts the server on {@code "localhost"} selects an unused port.
   *
   * <p>
   * Use {@link Builder} if you need to specify any of the optional parameters.
   * </p>
   */
  public MyLocalServerReceiver() {
    this("localhost", -1);
  }

  /**
   * Constructor.
   *
   * @param host Host name to use
   * @param port Port to use or {@code -1} to select an unused port
   */
  MyLocalServerReceiver(String host, int port) {
    this.host = host;
    this.port = port;
  }

  @Override
  public String getRedirectUri() throws IOException {
    if (port == -1) {
      port = getUnusedPort();
    }
    server = new Server(port);
    for (Connector c : server.getConnectors()) {
      c.setHost(host);
    }
    server.addHandler(new CallbackHandler());
    try {
      server.start();
    } catch (Exception e) {
      Throwables.propagateIfPossible(e);
      throw new IOException(e);
    }
    return "http://" + host + ":" + port + CALLBACK_PATH;
  }

  @Override
  public String waitForCode() throws IOException {
    lock.lock();
    try {
      while (code == null && error == null) {
        gotAuthorizationResponse.awaitUninterruptibly();
      }
      if (error != null) {
        throw new IOException("User authorization failed (" + error + ")");
      }
      return code;
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void stop() throws IOException {
    if (server != null) {
      try {
        server.stop();
      } catch (Exception e) {
        Throwables.propagateIfPossible(e);
        throw new IOException(e);
      }
      server = null;
    }
  }

  /** Returns the host name to use. */
  public String getHost() {
    return host;
  }

  /**
   * Returns the port to use or {@code -1} to select an unused port in {@link #getRedirectUri()}.
   */
  public int getPort() {
    return port;
  }

  private static int getUnusedPort() throws IOException {
    Socket s = new Socket();
    s.bind(null);
    try {
      return s.getLocalPort();
    } finally {
      s.close();
    }
  }

  /**
   * Builder.
   *
   * <p>
   * Implementation is not thread-safe.
   * </p>
   */
  public static final class Builder {

    /** Host name to use. */
    private String host = "localhost";

    /** Port to use or {@code -1} to select an unused port. */
    private int port = -1;

    /** Builds the {@link LocalServerReceiver}. */
    public MyLocalServerReceiver build() {
      return new MyLocalServerReceiver(host, port);
    }

    /** Returns the host name to use. */
    public String getHost() {
      return host;
    }

    /** Sets the host name to use. */
    public Builder setHost(String host) {
      this.host = host;
      return this;
    }

    /** Returns the port to use or {@code -1} to select an unused port. */
    public int getPort() {
      return port;
    }

    /** Sets the port to use or {@code -1} to select an unused port. */
    public Builder setPort(int port) {
      this.port = port;
      return this;
    }
  }

  /**
   * Jetty handler that takes the verifier token passed over from the OAuth provider and stashes it
   * where {@link #waitForCode} will find it.
   */
  class CallbackHandler extends AbstractHandler {

    @Override
    public void handle(
        String target, HttpServletRequest request, HttpServletResponse response, int dispatch)
        throws IOException {
      if (!CALLBACK_PATH.equals(target)) {
        return;
      }
      writeLandingHtml(response);
      response.flushBuffer();
      ((Request) request).setHandled(true);
      lock.lock();
      try {
        error = request.getParameter("error");
        code = request.getParameter("code");
        gotAuthorizationResponse.signal();
      } finally {
        lock.unlock();
      }
    }

    private void writeLandingHtml(HttpServletResponse response) throws IOException {
      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType("text/html");

      PrintWriter doc = response.getWriter();
      doc.println("<html>");
      doc.println("<head><title>OAuth 2.0 Authentication Token Recieved</title></head>");
      doc.println("<body>");
      doc.println("Received verification code. Closing...");
      doc.println("<script type='text/javascript'>");
      // We open "" in the same window to trigger JS ownership of it, which lets
      // us then close it via JS, at least in Chrome.
      doc.println("window.setTimeout(function() {");
      doc.println("    window.open('', '_self', ''); window.close(); }, 1000);");
      doc.println("if (window.opener) { window.opener.checkToken(); }");
      doc.println("</script>");
      doc.println("</body>");
      doc.println("</HTML>");
      doc.flush();
    }
  }
}

