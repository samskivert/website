/*
 * Christmas.java
 * prints out 'the twelve days of christmas' if you can believe that.
 * ported to Java by Paul Phillips <paulp@go2net.com> (with an assist
 * at the end by mdb@go2net.com to figure out a way around Java not
 * having a comma operator and adding a little AWT GUI)
 */

import java.applet.Applet;

import java.awt.BorderLayout;
import java.awt.TextArea;

public class Christmas extends Applet implements Runnable
{
  TextArea _out = null;
  Thread _thread;

  public void init ()
  {
    setLayout(new BorderLayout());
    _out = new TextArea();
    add("Center", _out);
  }

  public void start ()
  {
    _thread = new Thread(this);
    _thread.start();
  }

  public void stop ()
  {
    _thread = null;
  }

  public void run ()
  {
    main(1, 0, null);
  }

  /* this code practically comments itself */
  public int main(int t, int _,char[] a)
  {
    return 1<t?(t<3?main(-79,-13,boink(a,main(-87,1-_,
    boink(a, main(-86,0,boink(a,1))))))&0:0)+(t<_?main(t+1,_,a)&0:0)+
    main(-94,-27+t,a)!=0&&t==2?_<13?
    main(2,_+1,null):9:16:t<0?t<-72?main(_,t,"@n'+,#'/*{}w+/w#cdnr/+,{}r/*de}+,/*{*+,/w{%+,/w#q#n+,/#{l+,/n{n+,/+#n+,/#;#q#n+,/+k#;*+,/'r :'d*'3,}{w+K w'K:'+}e#';dq#'l q#'+d'K#!/+k#;q#'r}eKK#}w'r}eKK{nl]'/#;#q#n'){)#}w'){){nl]'/+#n';d}rw' i;# ){nl]!/n{n#'; r{#w'r nc{nl]'/#{l,+'K {rw' iK{;[{nl]'/w#q#n'wk nw' iwk{KK{nl]!/w{%'l##w#' i; :{nl]'/*{q#'ld;r'}{nlwb!/*de}'c ;;{nl'-{}rw]'/+,}##'*}#nc,',#nw]'/+kd'+e}+;#'rdq#w! nr'/ ') }+}{rl#'{n' ')#}'+}##(!!/".toCharArray())
    :t<-50?_==(a[0])?putchar(a[31]):main(-65,_,boink(a,1)):
    main(((a[0])=='/'?1:0)+t,_,boink(a,1))
    :0<t?main(2,2,null):(a[0])=='/'||(main(0,main(-61,a[0],
    "!ek;dc i@bK'(q)-[w]*%n+r3#l,{}:\nuwloca-O;m .vpbks,fxntdCeghiry".toCharArray()),boink(a,1))!=0)?1:0;
  }

  /* boink pretends to do pointer arithmetic */
  public char[] boink(char[] orig, int start) {
    if(orig == null) { return null; }
    return new String(orig, start, orig.length - start).toCharArray();
  }

  /* this isn't exactly what the "real" putchar does */
  int putchar(char c) { 
    if (_out == null) {
      System.out.print(c);
    } else {
      _out.appendText(new Character(c).toString());
    }
    return 27;
  }

  /* the "real" java main which just calls our ported main 
     with C-ish arguments (the key one being argc) */
  public static void main(String argv[]) {
    new Christmas().main(1, 0, null);
  }
}
