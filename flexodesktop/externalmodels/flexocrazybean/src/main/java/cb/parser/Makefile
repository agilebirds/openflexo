SOURCES := $(wildcard *.java)
CLASSES := $(patsubst %.java,%.class,$(SOURCES))
#PARSERFILES := 	ASCII_CharStream.java PetalParserConstants.java\
#	       	PetalParserTokenManager.java ParseError.java ParseException.java\
#		PetalParser.java TokenMgrError.java

JC 	= jikes
JFLAGS  = +E

%.class: %.java
	@rm -f $@
	$(JC) $(JFLAGS) $<

all: PetalParser.class $(CLASSES)

#PetalParser.java: PetalParser.jj	
#	javacc PetalParser.jj

clean:
	rm -f TMP *~ *.class #*# 
