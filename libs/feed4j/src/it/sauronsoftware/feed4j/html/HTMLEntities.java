package it.sauronsoftware.feed4j.html;

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class encodes and decodes HTML entities in strings.
 * 
 * @author Carlo Pelliccia
 */
class HTMLEntities {

	/**
	 * HTML entities table.
	 */
	private static Hashtable entities = new Hashtable();

	/**
	 * Reversed HTML entities table
	 */
	private static Hashtable reversedEntities = new Hashtable();

	/**
	 * Entities recognition pattern.
	 */
	private static Pattern pattern = Pattern.compile("&(#?[a-zA-Z0-9]+);");

	static {
		entities.put("nbsp", " ");
		entities.put("iexcl", "\u00A1");
		entities.put("cent", "\u00A2");
		entities.put("pound", "\u00A3");
		entities.put("curren", "\u00A4");
		entities.put("yen", "\u00A5");
		entities.put("brvbar", "\u00A6");
		entities.put("sect", "\u00A7");
		entities.put("uml", "\u00A8");
		entities.put("copy", "\u00A9");
		entities.put("ordf", "\u00AA");
		entities.put("laquo", "\u00AB");
		entities.put("not", "\u00AC");
		entities.put("shy", "\u00AD");
		entities.put("reg", "\u00AE");
		entities.put("macr", "\u00AF");
		entities.put("deg", "\u00B0");
		entities.put("plusmn", "\u00B1");
		entities.put("sup2", "\u00B2");
		entities.put("sup3", "\u00B3");
		entities.put("acute", "\u00B4");
		entities.put("micro", "\u00B5");
		entities.put("para", "\u00B6");
		entities.put("middot", "\u00B7");
		entities.put("cedil", "\u00B8");
		entities.put("sup1", "\u00B9");
		entities.put("ordm", "\u00BA");
		entities.put("raquo", "\u00BB");
		entities.put("frac14", "\u00BC");
		entities.put("frac12", "\u00BD");
		entities.put("frac34", "\u00BE");
		entities.put("iquest", "\u00BF");
		entities.put("Agrave", "\u00C0");
		entities.put("Aacute", "\u00C1");
		entities.put("Acirc", "\u00C2");
		entities.put("Atilde", "\u00C3");
		entities.put("Auml", "\u00C4");
		entities.put("Aring", "\u00C5");
		entities.put("AElig", "\u00C6");
		entities.put("Ccedil", "\u00C7");
		entities.put("Egrave", "\u00C8");
		entities.put("Eacute", "\u00C9");
		entities.put("Ecirc", "\u00CA");
		entities.put("Euml", "\u00CB");
		entities.put("Igrave", "\u00CC");
		entities.put("Iacute", "\u00CD");
		entities.put("Icirc", "\u00CE");
		entities.put("Iuml", "\u00CF");
		entities.put("ETH", "\u00D0");
		entities.put("Ntilde", "\u00D1");
		entities.put("Ograve", "\u00D2");
		entities.put("Oacute", "\u00D3");
		entities.put("Ocirc", "\u00D4");
		entities.put("Otilde", "\u00D5");
		entities.put("Ouml", "\u00D6");
		entities.put("times", "\u00D7");
		entities.put("Oslash", "\u00D8");
		entities.put("Ugrave", "\u00D9");
		entities.put("Uacute", "\u00DA");
		entities.put("Ucirc", "\u00DB");
		entities.put("Uuml", "\u00DC");
		entities.put("Yacute", "\u00DD");
		entities.put("THORN", "\u00DE");
		entities.put("szlig", "\u00DF");
		entities.put("agrave", "\u00E0");
		entities.put("aacute", "\u00E1");
		entities.put("acirc", "\u00E2");
		entities.put("atilde", "\u00E3");
		entities.put("auml", "\u00E4");
		entities.put("aring", "\u00E5");
		entities.put("aelig", "\u00E6");
		entities.put("ccedil", "\u00E7");
		entities.put("egrave", "\u00E8");
		entities.put("eacute", "\u00E9");
		entities.put("ecirc", "\u00EA");
		entities.put("euml", "\u00EB");
		entities.put("igrave", "\u00EC");
		entities.put("iacute", "\u00ED");
		entities.put("icirc", "\u00EE");
		entities.put("iuml", "\u00EF");
		entities.put("eth", "\u00F0");
		entities.put("ntilde", "\u00F1");
		entities.put("ograve", "\u00F2");
		entities.put("oacute", "\u00F3");
		entities.put("ocirc", "\u00F4");
		entities.put("otilde", "\u00F5");
		entities.put("ouml", "\u00F6");
		entities.put("divide", "\u00F7");
		entities.put("oslash", "\u00F8");
		entities.put("ugrave", "\u00F9");
		entities.put("uacute", "\u00FA");
		entities.put("ucirc", "\u00FB");
		entities.put("uuml", "\u00FC");
		entities.put("yacute", "\u00FD");
		entities.put("thorn", "\u00FE");
		entities.put("yuml", "\u00FF");
		entities.put("quot", "\"");
		entities.put("amp", "\u0026");
		entities.put("apos", "\u0027");
		entities.put("lt", "\u003C");
		entities.put("gt", "\u003E");
		entities.put("OElig", "\u0152");
		entities.put("oelig", "\u0153");
		entities.put("Scaron", "\u0160");
		entities.put("scaron", "\u0161");
		entities.put("Yuml", "\u0178");
		entities.put("circ", "\u02C6");
		entities.put("tilde", "\u02DC");
		entities.put("ensp", "\u2002");
		entities.put("emsp", "\u2003");
		entities.put("thinsp", "\u2009");
		entities.put("zwnj", "\u200C");
		entities.put("zwj", "\u200D");
		entities.put("lrm", "\u200E");
		entities.put("rlm", "\u200F");
		entities.put("ndash", "\u2013");
		entities.put("mdash", "\u2014");
		entities.put("lsquo", "\u2018");
		entities.put("rsquo", "\u2019");
		entities.put("sbquo", "\u201A");
		entities.put("ldquo", "\u201C");
		entities.put("rdquo", "\u201D");
		entities.put("bdquo", "\u201E");
		entities.put("dagger", "\u2020");
		entities.put("Dagger", "\u2021");
		entities.put("permil", "\u2030");
		entities.put("lsaquo", "\u2039");
		entities.put("rsaquo", "\u203A");
		entities.put("euro", "\u20AC");
		entities.put("fnof", "\u0192");
		entities.put("Alpha", "\u0391");
		entities.put("Beta", "\u0392");
		entities.put("Gamma", "\u0393");
		entities.put("Delta", "\u0394");
		entities.put("Epsilon", "\u0395");
		entities.put("Zeta", "\u0396");
		entities.put("Eta", "\u0397");
		entities.put("Theta", "\u0398");
		entities.put("Iota", "\u0399");
		entities.put("Kappa", "\u039A");
		entities.put("Lambda", "\u039B");
		entities.put("Mu", "\u039C");
		entities.put("Nu", "\u039D");
		entities.put("Xi", "\u039E");
		entities.put("Omicron", "\u039F");
		entities.put("Pi", "\u03A0");
		entities.put("Rho", "\u03A1");
		entities.put("Sigma", "\u03A3");
		entities.put("Tau", "\u03A4");
		entities.put("Upsilon", "\u03A5");
		entities.put("Phi", "\u03A6");
		entities.put("Chi", "\u03A7");
		entities.put("Psi", "\u03A8");
		entities.put("Omega", "\u03A9");
		entities.put("alpha", "\u03B1");
		entities.put("beta", "\u03B2");
		entities.put("gamma", "\u03B3");
		entities.put("delta", "\u03B4");
		entities.put("epsilon", "\u03B5");
		entities.put("zeta", "\u03B6");
		entities.put("eta", "\u03B7");
		entities.put("theta", "\u03B8");
		entities.put("iota", "\u03B9");
		entities.put("kappa", "\u03BA");
		entities.put("lambda", "\u03BB");
		entities.put("mu", "\u03BC");
		entities.put("nu", "\u03BD");
		entities.put("xi", "\u03BE");
		entities.put("omicron", "\u03BF");
		entities.put("pi", "\u03C0");
		entities.put("rho", "\u03C1");
		entities.put("sigmaf", "\u03C2");
		entities.put("sigma", "\u03C3");
		entities.put("tau", "\u03C4");
		entities.put("upsilon", "\u03C5");
		entities.put("phi", "\u03C6");
		entities.put("chi", "\u03C7");
		entities.put("psi", "\u03C8");
		entities.put("omega", "\u03C9");
		entities.put("thetasym", "\u03D1");
		entities.put("upsih", "\u03D2");
		entities.put("piv", "\u03D6");
		entities.put("bull", "\u2022");
		entities.put("hellip", "\u2026");
		entities.put("prime", "\u2032");
		entities.put("Prime", "\u2033");
		entities.put("oline", "\u203E");
		entities.put("frasl", "\u2044");
		entities.put("weierp", "\u2118");
		entities.put("image", "\u2111");
		entities.put("real", "\u211C");
		entities.put("trade", "\u2122");
		entities.put("alefsym", "\u2135");
		entities.put("larr", "\u2190");
		entities.put("uarr", "\u2191");
		entities.put("rarr", "\u2192");
		entities.put("darr", "\u2193");
		entities.put("harr", "\u2194");
		entities.put("crarr", "\u21B5");
		entities.put("lArr", "\u21D0");
		entities.put("uArr", "\u21D1");
		entities.put("rArr", "\u21D2");
		entities.put("dArr", "\u21D3");
		entities.put("hArr", "\u21D4");
		entities.put("forall", "\u2200");
		entities.put("part", "\u2202");
		entities.put("exist", "\u2203");
		entities.put("empty", "\u2205");
		entities.put("nabla", "\u2207");
		entities.put("isin", "\u2208");
		entities.put("notin", "\u2209");
		entities.put("ni", "\u220B");
		entities.put("prod", "\u220F");
		entities.put("sum", "\u2211");
		entities.put("minus", "\u2212");
		entities.put("lowast", "\u2217");
		entities.put("radic", "\u221A");
		entities.put("prop", "\u221D");
		entities.put("infin", "\u221E");
		entities.put("ang", "\u2220");
		entities.put("and", "\u2227");
		entities.put("or", "\u2228");
		entities.put("cap", "\u2229");
		entities.put("cup", "\u222A");
		entities.put("int", "\u222B");
		entities.put("there4", "\u2234");
		entities.put("sim", "\u223C");
		entities.put("cong", "\u2245");
		entities.put("asymp", "\u2248");
		entities.put("ne", "\u2260");
		entities.put("equiv", "\u2261");
		entities.put("le", "\u2264");
		entities.put("ge", "\u2265");
		entities.put("sub", "\u2282");
		entities.put("sup", "\u2283");
		entities.put("nsub", "\u2284");
		entities.put("sube", "\u2286");
		entities.put("supe", "\u2287");
		entities.put("oplus", "\u2295");
		entities.put("otimes", "\u2297");
		entities.put("perp", "\u22A5");
		entities.put("sdot", "\u22C5");
		entities.put("lceil", "\u2308");
		entities.put("rceil", "\u2309");
		entities.put("lfloor", "\u230A");
		entities.put("rfloor", "\u230B");
		entities.put("lang", "\u2329");
		entities.put("rang", "\u232A");
		entities.put("loz", "\u25CA");
		entities.put("spades", "\u2660");
		entities.put("clubs", "\u2663");
		entities.put("hearts", "\u2665");
		entities.put("diams", "\u2666");
		// Popola la tavola inversa.
		reversedEntities.put("\u00A0", "nbsp");
		reversedEntities.put("\u00A1", "iexcl");
		reversedEntities.put("\u00A2", "cent");
		reversedEntities.put("\u00A3", "pound");
		reversedEntities.put("\u00A4", "curren");
		reversedEntities.put("\u00A5", "yen");
		reversedEntities.put("\u00A6", "brvbar");
		reversedEntities.put("\u00A7", "sect");
		reversedEntities.put("\u00A8", "uml");
		reversedEntities.put("\u00A9", "copy");
		reversedEntities.put("\u00AA", "ordf");
		reversedEntities.put("\u00AB", "laquo");
		reversedEntities.put("\u00AC", "not");
		reversedEntities.put("\u00AD", "shy");
		reversedEntities.put("\u00AE", "reg");
		reversedEntities.put("\u00AF", "macr");
		reversedEntities.put("\u00B0", "deg");
		reversedEntities.put("\u00B1", "plusmn");
		reversedEntities.put("\u00B2", "sup2");
		reversedEntities.put("\u00B3", "sup3");
		reversedEntities.put("\u00B4", "acute");
		reversedEntities.put("\u00B5", "micro");
		reversedEntities.put("\u00B6", "para");
		reversedEntities.put("\u00B7", "middot");
		reversedEntities.put("\u00B8", "cedil");
		reversedEntities.put("\u00B9", "sup1");
		reversedEntities.put("\u00BA", "ordm");
		reversedEntities.put("\u00BB", "raquo");
		reversedEntities.put("\u00BC", "frac14");
		reversedEntities.put("\u00BD", "frac12");
		reversedEntities.put("\u00BE", "frac34");
		reversedEntities.put("\u00BF", "iquest");
		reversedEntities.put("\u00C0", "Agrave");
		reversedEntities.put("\u00C1", "Aacute");
		reversedEntities.put("\u00C2", "Acirc");
		reversedEntities.put("\u00C3", "Atilde");
		reversedEntities.put("\u00C4", "Auml");
		reversedEntities.put("\u00C5", "Aring");
		reversedEntities.put("\u00C6", "AElig");
		reversedEntities.put("\u00C7", "Ccedil");
		reversedEntities.put("\u00C8", "Egrave");
		reversedEntities.put("\u00C9", "Eacute");
		reversedEntities.put("\u00CA", "Ecirc");
		reversedEntities.put("\u00CB", "Euml");
		reversedEntities.put("\u00CC", "Igrave");
		reversedEntities.put("\u00CD", "Iacute");
		reversedEntities.put("\u00CE", "Icirc");
		reversedEntities.put("\u00CF", "Iuml");
		reversedEntities.put("\u00D0", "ETH");
		reversedEntities.put("\u00D1", "Ntilde");
		reversedEntities.put("\u00D2", "Ograve");
		reversedEntities.put("\u00D3", "Oacute");
		reversedEntities.put("\u00D4", "Ocirc");
		reversedEntities.put("\u00D5", "Otilde");
		reversedEntities.put("\u00D6", "Ouml");
		reversedEntities.put("\u00D7", "times");
		reversedEntities.put("\u00D8", "Oslash");
		reversedEntities.put("\u00D9", "Ugrave");
		reversedEntities.put("\u00DA", "Uacute");
		reversedEntities.put("\u00DB", "Ucirc");
		reversedEntities.put("\u00DC", "Uuml");
		reversedEntities.put("\u00DD", "Yacute");
		reversedEntities.put("\u00DE", "THORN");
		reversedEntities.put("\u00DF", "szlig");
		reversedEntities.put("\u00E0", "agrave");
		reversedEntities.put("\u00E1", "aacute");
		reversedEntities.put("\u00E2", "acirc");
		reversedEntities.put("\u00E3", "atilde");
		reversedEntities.put("\u00E4", "auml");
		reversedEntities.put("\u00E5", "aring");
		reversedEntities.put("\u00E6", "aelig");
		reversedEntities.put("\u00E7", "ccedil");
		reversedEntities.put("\u00E8", "egrave");
		reversedEntities.put("\u00E9", "eacute");
		reversedEntities.put("\u00EA", "ecirc");
		reversedEntities.put("\u00EB", "euml");
		reversedEntities.put("\u00EC", "igrave");
		reversedEntities.put("\u00ED", "iacute");
		reversedEntities.put("\u00EE", "icirc");
		reversedEntities.put("\u00EF", "iuml");
		reversedEntities.put("\u00F0", "eth");
		reversedEntities.put("\u00F1", "ntilde");
		reversedEntities.put("\u00F2", "ograve");
		reversedEntities.put("\u00F3", "oacute");
		reversedEntities.put("\u00F4", "ocirc");
		reversedEntities.put("\u00F5", "otilde");
		reversedEntities.put("\u00F6", "ouml");
		reversedEntities.put("\u00F7", "divide");
		reversedEntities.put("\u00F8", "oslash");
		reversedEntities.put("\u00F9", "ugrave");
		reversedEntities.put("\u00FA", "uacute");
		reversedEntities.put("\u00FB", "ucirc");
		reversedEntities.put("\u00FC", "uuml");
		reversedEntities.put("\u00FD", "yacute");
		reversedEntities.put("\u00FE", "thorn");
		reversedEntities.put("\u00FF", "yuml");
		reversedEntities.put("\"", "quot");
		reversedEntities.put("\u0026", "amp");
		// Some IE versions doesn't support the next entity, so it has been
		// disabled for convenience, although it is a standard one!
		// reversedEntities.put("\u0027", "apos");
		reversedEntities.put("\u003C", "lt");
		reversedEntities.put("\u003E", "gt");
		reversedEntities.put("\u0152", "OElig");
		reversedEntities.put("\u0153", "oelig");
		reversedEntities.put("\u0160", "Scaron");
		reversedEntities.put("\u0161", "scaron");
		reversedEntities.put("\u0178", "Yuml");
		reversedEntities.put("\u02C6", "circ");
		reversedEntities.put("\u02DC", "tilde");
		reversedEntities.put("\u2002", "ensp");
		reversedEntities.put("\u2003", "emsp");
		reversedEntities.put("\u2009", "thinsp");
		reversedEntities.put("\u200C", "zwnj");
		reversedEntities.put("\u200D", "zwj");
		reversedEntities.put("\u200E", "lrm");
		reversedEntities.put("\u200F", "rlm");
		reversedEntities.put("\u2013", "ndash");
		reversedEntities.put("\u2014", "mdash");
		reversedEntities.put("\u2018", "lsquo");
		reversedEntities.put("\u2019", "rsquo");
		reversedEntities.put("\u201A", "sbquo");
		reversedEntities.put("\u201C", "ldquo");
		reversedEntities.put("\u201D", "rdquo");
		reversedEntities.put("\u201E", "bdquo");
		reversedEntities.put("\u2020", "dagger");
		reversedEntities.put("\u2021", "Dagger");
		reversedEntities.put("\u2030", "permil");
		reversedEntities.put("\u2039", "lsaquo");
		reversedEntities.put("\u203A", "rsaquo");
		reversedEntities.put("\u20AC", "euro");
		reversedEntities.put("\u0192", "fnof");
		reversedEntities.put("\u0391", "Alpha");
		reversedEntities.put("\u0392", "Beta");
		reversedEntities.put("\u0393", "Gamma");
		reversedEntities.put("\u0394", "Delta");
		reversedEntities.put("\u0395", "Epsilon");
		reversedEntities.put("\u0396", "Zeta");
		reversedEntities.put("\u0397", "Eta");
		reversedEntities.put("\u0398", "Theta");
		reversedEntities.put("\u0399", "Iota");
		reversedEntities.put("\u039A", "Kappa");
		reversedEntities.put("\u039B", "Lambda");
		reversedEntities.put("\u039C", "Mu");
		reversedEntities.put("\u039D", "Nu");
		reversedEntities.put("\u039E", "Xi");
		reversedEntities.put("\u039F", "Omicron");
		reversedEntities.put("\u03A0", "Pi");
		reversedEntities.put("\u03A1", "Rho");
		reversedEntities.put("\u03A3", "Sigma");
		reversedEntities.put("\u03A4", "Tau");
		reversedEntities.put("\u03A5", "Upsilon");
		reversedEntities.put("\u03A6", "Phi");
		reversedEntities.put("\u03A7", "Chi");
		reversedEntities.put("\u03A8", "Psi");
		reversedEntities.put("\u03A9", "Omega");
		reversedEntities.put("\u03B1", "alpha");
		reversedEntities.put("\u03B2", "beta");
		reversedEntities.put("\u03B3", "gamma");
		reversedEntities.put("\u03B4", "delta");
		reversedEntities.put("\u03B5", "epsilon");
		reversedEntities.put("\u03B6", "zeta");
		reversedEntities.put("\u03B7", "eta");
		reversedEntities.put("\u03B8", "theta");
		reversedEntities.put("\u03B9", "iota");
		reversedEntities.put("\u03BA", "kappa");
		reversedEntities.put("\u03BB", "lambda");
		reversedEntities.put("\u03BC", "mu");
		reversedEntities.put("\u03BD", "nu");
		reversedEntities.put("\u03BE", "xi");
		reversedEntities.put("\u03BF", "omicron");
		reversedEntities.put("\u03C0", "pi");
		reversedEntities.put("\u03C1", "rho");
		reversedEntities.put("\u03C2", "sigmaf");
		reversedEntities.put("\u03C3", "sigma");
		reversedEntities.put("\u03C4", "tau");
		reversedEntities.put("\u03C5", "upsilon");
		reversedEntities.put("\u03C6", "phi");
		reversedEntities.put("\u03C7", "chi");
		reversedEntities.put("\u03C8", "psi");
		reversedEntities.put("\u03C9", "omega");
		reversedEntities.put("\u03D1", "thetasym");
		reversedEntities.put("\u03D2", "upsih");
		reversedEntities.put("\u03D6", "piv");
		reversedEntities.put("\u2022", "bull");
		reversedEntities.put("\u2026", "hellip");
		reversedEntities.put("\u2032", "prime");
		reversedEntities.put("\u2033", "Prime");
		reversedEntities.put("\u203E", "oline");
		reversedEntities.put("\u2044", "frasl");
		reversedEntities.put("\u2118", "weierp");
		reversedEntities.put("\u2111", "image");
		reversedEntities.put("\u211C", "real");
		reversedEntities.put("\u2122", "trade");
		reversedEntities.put("\u2135", "alefsym");
		reversedEntities.put("\u2190", "larr");
		reversedEntities.put("\u2191", "uarr");
		reversedEntities.put("\u2192", "rarr");
		reversedEntities.put("\u2193", "darr");
		reversedEntities.put("\u2194", "harr");
		reversedEntities.put("\u21B5", "crarr");
		reversedEntities.put("\u21D0", "lArr");
		reversedEntities.put("\u21D1", "uArr");
		reversedEntities.put("\u21D2", "rArr");
		reversedEntities.put("\u21D3", "dArr");
		reversedEntities.put("\u21D4", "hArr");
		reversedEntities.put("\u2200", "forall");
		reversedEntities.put("\u2202", "part");
		reversedEntities.put("\u2203", "exist");
		reversedEntities.put("\u2205", "empty");
		reversedEntities.put("\u2207", "nabla");
		reversedEntities.put("\u2208", "isin");
		reversedEntities.put("\u2209", "notin");
		reversedEntities.put("\u220B", "ni");
		reversedEntities.put("\u220F", "prod");
		reversedEntities.put("\u2211", "sum");
		reversedEntities.put("\u2212", "minus");
		reversedEntities.put("\u2217", "lowast");
		reversedEntities.put("\u221A", "radic");
		reversedEntities.put("\u221D", "prop");
		reversedEntities.put("\u221E", "infin");
		reversedEntities.put("\u2220", "ang");
		reversedEntities.put("\u2227", "and");
		reversedEntities.put("\u2228", "or");
		reversedEntities.put("\u2229", "cap");
		reversedEntities.put("\u222A", "cup");
		reversedEntities.put("\u222B", "int");
		reversedEntities.put("\u2234", "there4");
		reversedEntities.put("\u223C", "sim");
		reversedEntities.put("\u2245", "cong");
		reversedEntities.put("\u2248", "asymp");
		reversedEntities.put("\u2260", "ne");
		reversedEntities.put("\u2261", "equiv");
		reversedEntities.put("\u2264", "le");
		reversedEntities.put("\u2265", "ge");
		reversedEntities.put("\u2282", "sub");
		reversedEntities.put("\u2283", "sup");
		reversedEntities.put("\u2284", "nsub");
		reversedEntities.put("\u2286", "sube");
		reversedEntities.put("\u2287", "supe");
		reversedEntities.put("\u2295", "oplus");
		reversedEntities.put("\u2297", "otimes");
		reversedEntities.put("\u22A5", "perp");
		reversedEntities.put("\u22C5", "sdot");
		reversedEntities.put("\u2308", "lceil");
		reversedEntities.put("\u2309", "rceil");
		reversedEntities.put("\u230A", "lfloor");
		reversedEntities.put("\u230B", "rfloor");
		reversedEntities.put("\u2329", "lang");
		reversedEntities.put("\u232A", "rang");
		reversedEntities.put("\u25CA", "loz");
		reversedEntities.put("\u2660", "spades");
		reversedEntities.put("\u2663", "clubs");
		reversedEntities.put("\u2665", "hearts");
		reversedEntities.put("\u2666", "diams");
	}

	/**
	 * This method decodes every HTML entity found in a string.
	 * 
	 * @param str
	 *            The string.
	 * @return A new string with HTML entities decoded.
	 */
	public static String decode(String str) {
		StringBuffer buffer = new StringBuffer();
		Matcher matcher = pattern.matcher(str);
		int index = 0;
		while (matcher.find()) {
			int start = matcher.start() + 1;
			int end = matcher.end() - 1;
			if (start > index) {
				buffer.append(str.substring(index, start - 1));
			}
			String entity = str.substring(start, end);
			if (entity.charAt(0) == '#') {
				try {
					buffer.append((char) Integer.parseInt(entity.substring(1,
							entity.length())));
				} catch (Throwable t) {
					;
				}
			} else {
				String value = (String) entities.get(entity);
				if (value != null) {
					buffer.append(value);
				}
			}
			index = end + 1;
		}
		int strLength = str.length();
		if (index < strLength) {
			buffer.append(str.substring(index, strLength));
		}
		return buffer.toString();
	}

	/**
	 * This method encodes a plain text string applying the known HTML entities.
	 * 
	 * @param str
	 *            The string.
	 * @return A new string with HTML entities encoded.
	 */
	public static String encode(String str) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			String key = String.valueOf(c);
			String entity = (String) reversedEntities.get(key);
			if (entity != null) {
				buffer.append("&");
				buffer.append(entity);
				buffer.append(";");
			} else {
				int ci = 0xffff & c;
				if (ci < 160)
					buffer.append(c);
				else {
					buffer.append("&#");
					buffer.append(ci);
					buffer.append(';');
				}
			}
		}
		return buffer.toString();
	}

}
