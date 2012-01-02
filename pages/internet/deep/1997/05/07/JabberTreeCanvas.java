/**
 * Displays a WalTreeCanvas with static Jabberwocky text for illustration.
 */
public class JabberTreeCanvas extends WalTreeCanvas {
    public JabberTreeCanvas() {
	super();

	setTree(makeJabberTree());
    }

    WNode makeJabberTree() {
        WNode t;

        t = makeNode("frabjous", null, null, null);
        t.child = makeNode("borogoves", t, null, null);
        t.child.sibling = makeNode("vorpal", t, null, null);
        t.child.sibling.sibling = makeNode("brillig", t, null, null);
        t.child.sibling.sibling.child = makeNode("mome", 
	  t.child.sibling.sibling, null, null);
        t.child.sibling.sibling.child.sibling = makeNode("raths", 
	  t.child.sibling.sibling, null, null);
        t.child.child = makeNode("frumious", t.child, null, null);
        t.child.child.sibling = makeNode("Bandersnatch", t.child, 
	  null, null);
        t.child.child.sibling.sibling = makeNode("mimsy", 
	  t.child, null, null);

        return t;
    }
};
