To create a new packaging, you need to set up the followings:
* In project flexodesktop/GUI/flexo, add a user type of your choice in class org.openflexo.module.UserType
* Modify the method: org.openflexo.module.UserType.getUserTypeNamed(String) to handle your new UserType.
* Create a directory with the appropriate license files in flexodesktop/GUI/flexo/src/main/resources/License
* Create a new directory in packaging/exec/${your.module.name}
* Create a pom.xml (based on one of the other modules) and add proper dependency. You automatically inherit of dependency 
  towards flexodeskop/GUI/flexo.
* Add your new module in packaging/exec/pom.xml. With this you have now created a new module/packaging.
* In packaging/flexobuild add a property file named "${your.module.name}.properties" (values needs to match various elements 
  described above (userType, productSuffix (for Licence path), and maven.artifact.id (must match your pom.xml artifcatId))
* In build.xml create a new target "package${userType}" and invoke it from target "package" in build.xml of project packaging/flexobuild
* In the pom.xml file of packaging/flexobuild, add a dependency towards the pom of your new module