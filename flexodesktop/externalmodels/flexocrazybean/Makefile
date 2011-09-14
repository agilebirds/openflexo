#
# $Id: Makefile,v 1.1 2011/05/24 00:54:40 gpolet Exp $
#
SUBDIRS  := cb/parser cb/util cb/petal cb/generator test
PACKAGES := $(patsubst cb/%,cb.%,$(SUBDIRS))
EXAMPLES := $(wildcard examples/*.mdl)
JAVADOC  := javadoc

JC     = jikes
JFLAGS = +E

all:
	for i in $(SUBDIRS) ;\
	do\
		(cd $$i ; $(MAKE) JC=$(JC) JFLAGS=$(JFLAGS))\
	done

check:	always
	@for i in $(EXAMPLES) ;\
	do\
		echo Checking $$i ;\
		java -cp . cb.parser.PrintVisitor $$i > TMP ;\
		echo File successfully parsed ;\
		diff -b $$i TMP ;\
	done
	@rm -f TMP

test:	all
	(cd test ; $(MAKE) test)

always:

templates: all
	java cb.util.Dump

docs:	always
	-mkdir api-docs
	$(JAVADOC) -classpath . -doctitle "CrazyBeans API documentation" \
	-windowtitle "CrazyBeans API documentation" -version \
	-author -d api-docs $(PACKAGES)
clean:
	rm -f TMP
	for i in $(SUBDIRS) ;\
	do\
		(cd $$i ; $(MAKE) clean)\
	done
