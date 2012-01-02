JAVAC = javac
JFLAGS = -classpath /usr/local/java/lib/classes.zip:..

SRCS = \
	Client.java \
	ClientManager.java \
	DObjectManager.java \
	Server.java

OBJS = $(SRCS:%.java=%.class)

all: $(OBJS)

%.class: %.java
	$(JAVAC) $(JFLAGS) $*.java

clean:
	$(RM) $(RMFLAGS) *.class
