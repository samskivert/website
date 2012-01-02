JAVAC = javac
JFLAGS = -classpath /usr/local/java/lib/classes.zip:..

SRCS = \
	Board.java \
	WordGame.java \
	WordGameGame.java \
	CrossOutPanel.java \
	Node.java \
	Pieces.java \
	PlayerPanel.java \
	Verifier.java \
	WordList.java \
	WordPanel.java

OBJS = $(SRCS:%.java=%.class)

all: $(OBJS)

%.class: %.java
	$(JAVAC) $(JFLAGS) $*.java

clean:
	$(RM) $(RMFLAGS) *.class
