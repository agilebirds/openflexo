#!/bin/bash
java @vm.args@ -classpath lib/\* @main.class@ -userType @userType@
