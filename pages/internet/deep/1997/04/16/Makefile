JAVAC = javac
JFLAGS =

SRCS = \
	Base.java \
	Circles.java \
	Deco.java \
	Dragon.java \
	Grid.java \
	Plasma.java \
	Spiral.java \
	TestPattern.java

OBJS = $(SRCS:%.java=%.class)

all: $(OBJS)

%.class: %.java
	$(JAVAC) $(JFLAGS) $*.java
