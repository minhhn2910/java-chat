/*
 * this file include all class for process smile
 * popup menu when right click in text field
 * drag and drop file send
 */
package p2pchat2012;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.*;
import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

/**
 *
 * @author minh
 */
public class smile_process {
    	static String st1=null;
	static String st2=null;
	static String st3=null;
        static int m = -1;
	/**
	 * @param args
	 */
	public static void smile_process()
        {
            st1=null;
            st2=null;
            st3=null;
            m=-1;
        }
        public String getstr1()
        {
            return st1;
        }
          public String getstr2()
        {
            return st2;
        }
            public String getstr3()
        {
            return st3;
        }
        public int getindex()
       {return m;}
	static void parse_string(String s){
             if(s == null || s=="")
            {   m = -1;
                return;
            }
            int check=0;
            int n= s.length();
	    char[] ch=s.toCharArray();
	    for(int i=0;i<(n-5);i++)
                {
                    if(ch[i]==':'&& ch[i+1]=='('&&ch[i+2]=='s'&&ch[i+3]==')')
                    {
                        if(ch[i+4] < '0' || ch[i+4] > '9'||ch[i+5] < '0' || ch[i+5] > '9')
                        {
                            i++;
                            continue;
                        }
                        if(ch[i+4] == '0')
                          st2 = s.substring(i+5, i+6);
                        else
                          st2 = s.substring(i+4, i+6); 
                          st1=s.substring(0,i);
                          st3=s.substring(i+6,n);
                          m= Integer.parseInt(st2);
                        check=1;
                        break;
                    }
                 }
                if(check!=1)
                {
                    st1 = s.substring(0,n);
                    st2 = null;
                    st3= null;
                }
        }
}

class MyEventQueue extends EventQueue{ 
    protected void dispatchEvent(AWTEvent event){ 
        super.dispatchEvent(event); 
        // interested only in mouseevents 
        if(!(event instanceof MouseEvent)) 
            return; 
       MouseEvent me = (MouseEvent)event; 
       // interested only in popuptriggers 
        if(!me.isPopupTrigger()) 
            return; 
        // me.getComponent(...) retunrs the heavy weight component on which event occured 
        Component comp = SwingUtilities.getDeepestComponentAt(me.getComponent(), me.getX(), me.getY()); 
        // interested only in textcomponents 
        if(!(comp instanceof JTextComponent)) 
            return; 
        // no popup shown by user code 
        if(MenuSelectionManager.defaultManager().getSelectedPath().length>0) 
            return; 
        // create popup menu and show 
        JTextComponent tc = (JTextComponent)comp; 
        JPopupMenu menu = new JPopupMenu(); 
        menu.add(new CutAction(tc)); 
        menu.add(new CopyAction(tc)); 
        menu.add(new PasteAction(tc)); 
        menu.add(new DeleteAction(tc)); 
        menu.addSeparator(); 
        menu.add(new SelectAllAction(tc)); 
        Point pt = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), tc);
        menu.show(tc, pt.x, pt.y);
    } 
} 

class CutAction extends AbstractAction{ 
    JTextComponent comp; 
 
    public CutAction(JTextComponent comp){ 
        super("Cut"); 
        this.comp = comp; 
    } 
    public void actionPerformed(ActionEvent e){ 
        comp.cut(); 
    } 
    public boolean isEnabled(){ 
        return comp.isEditable() 
                && comp.isEnabled() 
                && comp.getSelectedText()!=null; 
    } 
} 

class PasteAction extends AbstractAction{ 
    JTextComponent comp; 
 
    public PasteAction(JTextComponent comp){ 
        super("Paste"); 
        this.comp = comp; 
    } 
 
    public void actionPerformed(ActionEvent e){ 
        comp.paste(); 
    } 
 
    public boolean isEnabled(){ 
        if (comp.isEditable() && comp.isEnabled()){ 
            Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this); 
            return contents.isDataFlavorSupported(DataFlavor.stringFlavor); 
        }else 
            return false; 
    } 
} 
class DeleteAction extends AbstractAction{ 
    JTextComponent comp; 
 
    public DeleteAction(JTextComponent comp){ 
        super("Delete"); 
        this.comp = comp; 
    } 
 
    public void actionPerformed(ActionEvent e){ 
        comp.replaceSelection(null); 
    } 
 
    public boolean isEnabled(){ 
        return comp.isEditable() 
                && comp.isEnabled() 
                && comp.getSelectedText()!=null; 
    } 
} 
 
class CopyAction extends AbstractAction{ 
    JTextComponent comp; 
 
    public CopyAction(JTextComponent comp){ 
        super("Copy"); 
        this.comp = comp; 
    } 
 
    public void actionPerformed(ActionEvent e){ 
        comp.copy(); 
    } 
 
    public boolean isEnabled(){ 
        return comp.isEnabled() 
                && comp.getSelectedText()!=null; 
    } 
} 
class SelectAllAction extends AbstractAction{ 
    JTextComponent comp; 
 
    public SelectAllAction(JTextComponent comp){ 
        super("Select All"); 
        this.comp = comp; 
    } 
 
    public void actionPerformed(ActionEvent e){ 
        comp.selectAll(); 
    } 
 
    public boolean isEnabled(){ 
        return comp.isEnabled() 
                && comp.getText().length()>0; 
    } 
} 

//save setting to file

class readwritefile {
	public static String st1 = null ;
	public static String st2 =null;
	public static File s= new File("chat.setting");
    /*    public static void readwritefile()
        {
            st1 = null;
            st2 = null;
            s = new File("chat.setting");
        }
        
        */
        public static void reset_var()
        {
            st1 = null;
            st2 = null;
            s = new File("chat.setting");
        }
	static void readfile() {
		if(s.exists()) {
                    try{
			FileReader reader = new FileReader(s); 
			BufferedReader input= new BufferedReader(reader); 
			st1= new String(input.readLine());
			st2=new String(input.readLine());
                    }
                    catch(IOException ex){  }
		}
	}
	static void writefile(String wr1,String wr2) {
		try{
                FileOutputStream fos= new FileOutputStream("chat.setting",false);
		PrintWriter pw= new PrintWriter(fos); 
		pw.println(wr1);
		pw.println(wr2);
		pw.close();
                }
                catch (IOException ex){}
	}
}



