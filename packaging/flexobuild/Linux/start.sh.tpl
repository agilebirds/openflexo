#!/bin/bash
java @os.exec.args@ @logging.args@ @vm.args@ @debug.args@ -classpath lib/*.jar @main.class@ -userType @userType@