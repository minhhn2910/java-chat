/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package p2pchat2012;


/**
 *
 * @author minh
 */
public class font_process {
    
       public  font_setting base_setting;
       public font_setting parse_msg(font_setting my_set ,String msg)
        {
            base_setting = my_set;
             int n= msg.length();
	    char[] ch=msg.toCharArray();
            if(msg.equalsIgnoreCase("{{{{b}}}}"))
                base_setting.bold = true;
           else if(msg.equalsIgnoreCase("{{{{c}}}}"))
                 base_setting.bold = false;
           else if(msg.equalsIgnoreCase("{{{{i}}}}"))
                base_setting.italic= true;
           else if(msg.equalsIgnoreCase("{{{{j}}}}"))
                base_setting.italic= false;
           else if(msg.equalsIgnoreCase("{{{{u}}}}"))
                base_setting.underline= true;
           else if(msg.equalsIgnoreCase("{{{{t}}}}"))
                base_setting.underline= true;
           else if(n>=10 && ch[1]=='{')
            {    
              String st1 = msg.substring(0, 9);
                 if(st1.equalsIgnoreCase("{{{{f}}}}"))
                      base_setting.font_name=msg.substring(9,n);
              if(st1.equalsIgnoreCase("{{{{s}}}}"))
              {
                    if(n == 10)
                      base_setting.size = Integer.parseInt(ch[9]+"");
                    if(n == 11)
                   base_setting.size = Integer.parseInt(msg.substring(9,n));
               }
            }
                
            return base_setting;
        }
      
    
}