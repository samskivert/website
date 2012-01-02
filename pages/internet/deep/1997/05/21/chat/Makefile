JAVAC = javac
JFLAGS = -classpath /usr/local/java/lib/classes.zip:..

SRCS = \
	Chat.java \
	ChatPanel.java \
	WhoPanel.java

OBJS = $(SRCS:%.java=%.class)

all: $(OBJS)

%.class: %.java
	$(JAVAC) $(JFLAGS) $*.java

clean:
	$(RM) $(RMFLAGS) *.class
