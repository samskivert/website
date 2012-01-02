JAVAC  = javac
JFLAGS =

SRCS = \
	ControlPanel.java \
	Organism.java \
	Simulation.java \
	Environment.java \
	Population.java \
	Sortable.java \
	Generator.java \
	QuickSort.java \
	View.java \
	Language.java \
	SimApplet.java \
	ViewFrame.java

CLASSES = $(SRCS:.java=.class)

all: $(CLASSES)

%.class: %.java
	$(JAVAC) $(JFLAGS) $*.java
