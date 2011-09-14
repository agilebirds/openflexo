TEX	:= pdflatex
#EPS	:= $(wildcard eps/*.eps)
#JAVA	:= $(wildcard src/*.java reflect/*.java)
DISTILL := pstill

%.pdf: %.tex
	$(TEX) $<

%.ps: %.dvi
	dvips -o $@ $<

#%.pdf: %.ps
#	pstill -cc -gipt -o $@ $<

all: grammar.pdf

clean:
	rm -f core *.ps *~ *.bak *.aux *.dvi *.blg *.log *.class *.out
