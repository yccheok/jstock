!include x64.nsh

; Definitions for Java 1.7 Detection
!define JRE_VERSION "1.8"
; The URLs are obtained from https://www.java.com/en/download/manual.jsp
!define JRE_32_URL "https://github.com/yccheok/jstock/releases/download/jdk-8u251-windows/jdk-8u251-windows-i586.exe"
!define JRE_64_URL "https://github.com/yccheok/jstock/releases/download/jdk-8u251-windows/jdk-8u251-windows-x64.exe"
!define PRODUCT_NAME "JStock"
; The name of the installer
Name ${PRODUCT_NAME}

RequestExecutionLevel admin ;Workaround for Vista

; The file to write
OutFile "jstock-1.0.7.58-setup.exe"
LicenseData "gpl-2.0.txt"
 
; The default installation directory
InstallDir $PROGRAMFILES\${PRODUCT_NAME}
; The text to prompt the user to enter a directory
DirText "This will install JStock - Stock Market Software on your computer. Choose a directory"
Page license
page directory
Page instfiles
; The stuff to install
Section "" ;No components page, name is not important
SetShellVarContext all  ;Workaround for Vista
Call DetectJRE
; Set output path to the installation directory.
SetOutPath $INSTDIR
; Put file there
File /r jstock\jstock.exe
File /r jstock\config
File /r jstock\database
File /r jstock\lib
File /r jstock\docs
File /r jstock\extra
File chart.ico
CreateDirectory "$SMPROGRAMS\${PRODUCT_NAME}"
CreateShortCut "$SMPROGRAMS\${PRODUCT_NAME}\${PRODUCT_NAME}.lnk" "$INSTDIR\jstock.exe" "" "$INSTDIR\chart.ico"
CreateShortCut "$SMPROGRAMS\${PRODUCT_NAME}\Uninstall ${PRODUCT_NAME}.lnk" "$INSTDIR\Uninstall.exe" 
WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}" "DisplayName" "${PRODUCT_NAME} (remove only)"
WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}" "UninstallString" "$INSTDIR\Uninstall.exe"
; Tell the compiler to write an uninstaller and to look for a "Uninstall" section
WriteUninstaller $INSTDIR\Uninstall.exe
SectionEnd ; end the section
 ; The uninstall section
Section "Uninstall"
SetShellVarContext all  ;Workaround for Vista
RMDir /r $PROFILE\.jstock\indicator
RMDir /r $PROFILE\.jstock\config
RMDir /r $PROFILE\.jstock\history
RMDir /r $PROFILE\.jstock\logos
RMDir /r $PROFILE\.jstock\chat
RMDir /r $PROFILE\.jstock\extra
RMDir /r $PROFILE\.jstock
RMDir /r $INSTDIR\lib
RMDir /r $INSTDIR\config
RMDir /r $INSTDIR\database
RMDir /r $INSTDIR\chat
RMDir /r $INSTDIR\extra
RMDir /r $INSTDIR\docs
Delete $INSTDIR\chart.ico
Delete $INSTDIR\jstock.exe
Delete $INSTDIR\Uninstall.exe
Delete "$SMPROGRAMS\${PRODUCT_NAME}\Uninstall ${PRODUCT_NAME}.lnk"
Delete "$SMPROGRAMS\${PRODUCT_NAME}\${PRODUCT_NAME}.lnk"
RMDIR /r "$SMPROGRAMS\${PRODUCT_NAME}"
DeleteRegKey HKEY_LOCAL_MACHINE "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"
# Do not user /r flag, to avoid from deleting entire system directory.
RMDir $INSTDIR
SectionEnd
Function VersionCompare
    !define VersionCompare `!insertmacro VersionCompareCall`
 
    !macro VersionCompareCall _VER1 _VER2 _RESULT
        Push `${_VER1}`
        Push `${_VER2}`
        Call VersionCompare
        Pop ${_RESULT}
    !macroend
 
    Exch $1
    Exch
    Exch $0
    Exch
    Push $2
    Push $3
    Push $4
    Push $5
    Push $6
    Push $7
 
    begin:
    StrCpy $2 -1
    IntOp $2 $2 + 1
    StrCpy $3 $0 1 $2
    StrCmp $3 '' +2
    StrCmp $3 '.' 0 -3
    StrCpy $4 $0 $2
    IntOp $2 $2 + 1
    StrCpy $0 $0 '' $2
 
    StrCpy $2 -1
    IntOp $2 $2 + 1
    StrCpy $3 $1 1 $2
    StrCmp $3 '' +2
    StrCmp $3 '.' 0 -3
    StrCpy $5 $1 $2
    IntOp $2 $2 + 1
    StrCpy $1 $1 '' $2
 
    StrCmp $4$5 '' equal
 
    StrCpy $6 -1
    IntOp $6 $6 + 1
    StrCpy $3 $4 1 $6
    StrCmp $3 '0' -2
    StrCmp $3 '' 0 +2
    StrCpy $4 0
 
    StrCpy $7 -1
    IntOp $7 $7 + 1
    StrCpy $3 $5 1 $7
    StrCmp $3 '0' -2
    StrCmp $3 '' 0 +2
    StrCpy $5 0
 
    StrCmp $4 0 0 +2
    StrCmp $5 0 begin newer2
    StrCmp $5 0 newer1
    IntCmp $6 $7 0 newer1 newer2
 
    StrCpy $4 '1$4'
    StrCpy $5 '1$5'
    IntCmp $4 $5 begin newer2 newer1
 
    equal:
    StrCpy $0 0
    goto end
    newer1:
    StrCpy $0 1
    goto end
    newer2:
    StrCpy $0 2
 
    end:
    Pop $7
    Pop $6
    Pop $5
    Pop $4
    Pop $3
    Pop $2
    Pop $1
    Exch $0
FunctionEnd
Function GetJRE
    StrCpy $2 "$TEMP\Java Runtime Environment.exe"
    ${If} ${RunningX64}   
        # 64 bit code
        MessageBox MB_OK "${PRODUCT_NAME} uses 64-bit Java ${JRE_VERSION}, it will now \
                         be downloaded and installed"        
        inetc::get ${JRE_64_URL} $2
    ${Else}
        MessageBox MB_OK "${PRODUCT_NAME} uses 32-bit Java ${JRE_VERSION}, it will now \
                         be downloaded and installed"        
        inetc::get ${JRE_32_URL} $2
    ${EndIf}
    Pop $R0 ;Get the return value
        StrCmp $R0 "OK" +3
        MessageBox MB_OK "Download failed: $R0"
        Quit
    ExecWait $2
    Delete $2
FunctionEnd
 
 
Function DetectJRE
    ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" \
             "CurrentVersion"
    ${VersionCompare} ${JRE_VERSION} $2 $R0
    ; $R0="0" if versions are equal
    ; $R0="1" if JRE_VERSION is newer
    ; $R0="2" if JRE_VERSION is older

    StrCmp $R0 0 done
    StrCmp $R0 2 done
    
    # Try for 64 bit registry.
    SetRegView 64
    ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" \
             "CurrentVersion"
    SetRegView 32
             
    ${VersionCompare} ${JRE_VERSION} $2 $R0
    ; $R0="0" if versions are equal
    ; $R0="1" if JRE_VERSION is newer
    ; $R0="2" if JRE_VERSION is older

    StrCmp $R0 0 done
    StrCmp $R0 2 done
             
    Call GetJRE
  
    done:
FunctionEnd

 Function un.onInit
    ; "No" button by default.
    MessageBox MB_YESNO|MB_DEFBUTTON2 "This will uninstall JStock. Press $\"Yes$\" to continue." IDYES NoAbort
        Abort ; causes uninstaller to quit.
    NoAbort:
 FunctionEnd