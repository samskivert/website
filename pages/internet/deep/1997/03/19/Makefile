JAVAC = javac
JFLAGS =

BUILTINS = \
	Badd.java \
	Bdefun.java \
	Bdiv.java \
	Beq.java \
	Bgt.java \
	Bgte.java \
	Bif.java \
	Blt.java \
	Blte.java \
	Bmul.java \
	Bprint.java \
	Bprintln.java \
	Bsetq.java \
	Bsub.java \
	Bwhile.java

SRCS = \
	Function.java \
	Interpreter.java \
	LispApplet.java \
	List.java \
	ParseException.java \
	Parser.java \
	RunTimeException.java \
	Scanner.java \
	TextAreaOutputStream.java \
	UserFunction.java \
	$(BUILTINS)

OBJS = $(SRCS:%.java=%.class)

all: $(OBJS)

%.class: %.java
	$(JAVAC) $(JFLAGS) $*.java

clean:
	$(RM) $(RMFLAGS) *.class
