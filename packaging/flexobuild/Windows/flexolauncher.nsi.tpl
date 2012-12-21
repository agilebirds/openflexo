; Java Launcher with automatic JRE installation
;-----------------------------------------------
 
Name "@product.name@ Launcher"
Caption "@product.name@ Launcher"
Icon "@dist.dir@\Icons\Flexo\@wizard.setup.icon@"
OutFile "@dist.dir@\@qualified_and_versioned_app_name@.exe"
 
VIAddVersionKey "ProductName" "@product.name@ Launcher"
VIAddVersionKey "Comments" "This executable launches @product.name@ and prefetches Java if needed"
VIAddVersionKey "CompanyName" "@organization.name@"
VIAddVersionKey "LegalTrademarks" "@organization.name@"
VIAddVersionKey "LegalCopyright" "@organization.name@"
VIAddVersionKey "FileDescription" "This executable launches @product.name@ and prefetches Java if needed"
VIAddVersionKey "FileVersion" "1.0.0"
VIProductVersion "1.0.0.1"
 
!define CLASSPATH "lib/*;"
!define CLASS "@main.class@"
!define PRODUCT_NAME "Flexo"
 
; Definitions for Java 6.0
!define JRE_VERSION "6.0"
!define JRE_URL "http://javadl.sun.com/webapps/download/AutoDL?BundleId=49024"
 
!define JAVAEXE "javaw.exe"
 
RequestExecutionLevel user
SilentInstall silent
AutoCloseWindow true
ShowInstDetails nevershow
 
!include "FileFunc.nsh"
!insertmacro GetFileVersion
!insertmacro GetParameters
!include "WordFunc.nsh"
!include "UAC.nsh"
!insertmacro VersionCompare
 
Section ""
  Call GetJRE
  Pop $R0
 
  ; change for your purpose (-jar etc.)
  ${GetParameters} $1
  StrCpy $0 '"$R0" @vm.args@ -classpath "${CLASSPATH}" ${CLASS} $1 @program.args@'
 
  SetOutPath $EXEDIR
  Exec $0
SectionEnd
 
;  returns the full path of a valid java.exe
;  looks in:
;  1 - .\jre directory (JRE Installed with application)
;  2 - JAVA_HOME environment variable
;  3 - the registry
;  4 - hopes it is in current dir or PATH
Function GetJRE
    Push $R0
    Push $R1
    Push $2
 
  ; 1) Check local JRE
  CheckLocal:
    ClearErrors
    StrCpy $R0 "$EXEDIR\jre\bin\${JAVAEXE}"
    IfFileExists $R0 JreFound
 
  ; 2) Check for JAVA_HOME
  CheckJavaHome:
    ClearErrors
    ReadEnvStr $R0 "JAVA_HOME"
    StrCpy $R0 "$R0\bin\${JAVAEXE}"
    IfErrors CheckRegistry     
    IfFileExists $R0 0 CheckRegistry
    Call CheckJREVersion
    IfErrors CheckRegistry JreFound
 
  ; 3) Check for registry
  CheckRegistry:
    ClearErrors
    ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
    ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
    StrCpy $R0 "$R0\bin\${JAVAEXE}"
    IfErrors DownloadJRE
    IfFileExists $R0 0 DownloadJRE
    Call CheckJREVersion
    IfErrors DownloadJRE JreFound
 
  DownloadJRE:
    Call ElevateToAdmin
    MessageBox MB_ICONINFORMATION "${PRODUCT_NAME} uses Java Runtime Environment ${JRE_VERSION}, it will now be downloaded and installed."
    StrCpy $2 "$TEMP\Java Runtime Environment.exe"
    nsisdl::download /TIMEOUT=30000 ${JRE_URL} $2
    Pop $R0 ;Get the return value
    StrCmp $R0 "success" +3
      MessageBox MB_ICONSTOP "Download failed: $R0"
      Abort
    ExecWait $2
    Delete $2
 
    ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
    ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
    StrCpy $R0 "$R0\bin\${JAVAEXE}"
    IfFileExists $R0 0 GoodLuck
    Call CheckJREVersion
    IfErrors GoodLuck JreFound
 
  ; 4) wishing you good luck
  GoodLuck:
    StrCpy $R0 "${JAVAEXE}"
    ; MessageBox MB_ICONSTOP "Cannot find appropriate Java Runtime Environment."
    ; Abort
 
  JreFound:
    Pop $2
    Pop $R1
    Exch $R0
FunctionEnd
 
; Pass the "javaw.exe" path by $R0
Function CheckJREVersion
    Push $R1
 
    ; Get the file version of javaw.exe
    ${GetFileVersion} $R0 $R1
    ${VersionCompare} ${JRE_VERSION} $R1 $R1
 
    ; Check whether $R1 != "1"
    ClearErrors
    StrCmp $R1 "1" 0 CheckDone
    SetErrors
 
  CheckDone:
    Pop $R1
FunctionEnd
 
; Attempt to give the UAC plug-in a user process and an admin process.
Function ElevateToAdmin
  UAC_Elevate:
    !insertmacro UAC_RunElevated
    StrCmp 1223 $0 UAC_ElevationAborted ; UAC dialog aborted by user?
    StrCmp 0 $0 0 UAC_Err ; Error?
    StrCmp 1 $1 0 UAC_Success ;Are we the real deal or just the wrapper?
    Quit
 
  UAC_ElevationAborted:
    # elevation was aborted, run as normal?
    MessageBox MB_ICONSTOP "This installer requires admin access, aborting!"
    Abort
 
  UAC_Err:
    MessageBox MB_ICONSTOP "Unable to elevate, error $0"
    Abort
 
  UAC_Success:
    StrCmp 1 $3 +4 ;Admin?
    StrCmp 3 $1 0 UAC_ElevationAborted ;Try again?
    MessageBox MB_ICONSTOP "This installer requires admin access, try again"
    goto UAC_Elevate 
FunctionEnd