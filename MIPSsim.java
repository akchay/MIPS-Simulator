import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.ArrayList;
// On my honor, I have neither given nor received unathourized aid on this assignment.

public class MIPSsim
{
	
	StringBuilder output = new StringBuilder ();
	StringBuilder output2 = new StringBuilder ();
	int counter,c=1, intindex, lines, counter2;
	String newkey,newvalue, count, index, instruction1, instruction2, parts1[], parts2[], parts3[], parts4[];
	int []reg = new int[32];
	int []issueregisterstatus = new int [32];
	int []registerstatus = new int [32];
	int [] checkarray = new int [32];
	int [] checkarray2 = new int [32];
	boolean stalled = false, discarded = false, br, waiting = false, found;
	int dest,src1,src2, flag, flag2;
	
	
	
	HashMap<String, String> map2 = new HashMap<String, String>();
	static HashMap<String, String> map3 = new HashMap<String, String>();
	int [] preIssueRD = new int [32];
	int [] preIssueWD = new int [32];
	HashMap<String,Integer> RD = new HashMap<String,Integer>();
	HashMap<String,Integer> WD = new HashMap<String,Integer>();
	
	ArrayList<String> WaitingInstr =  new ArrayList<String>(1);
	ArrayList<String> ExecutedInstr = new ArrayList<String>(1);
	ArrayList<String> PREISSUEUP = new ArrayList<String>(4);
	ArrayList<String> PREISSUEDOWN = new ArrayList<String>(4);
	ArrayList<String> PREALUUP = new ArrayList<String>(2);
	ArrayList<String> PREALUDOWN = new ArrayList<String>(2);
	ArrayList<String> PREMEMUP = new ArrayList<String>(1);
	ArrayList<String> PREMEMDOWN = new ArrayList<String>(1);
	ArrayList<String> POSTMEMUP = new ArrayList<String>(1);	
	ArrayList<String> POSTMEMDOWN = new ArrayList<String>(1);
	ArrayList<String> POSTALUUP = new ArrayList<String>(1);
	ArrayList<String> POSTALUDOWN = new ArrayList<String>(1);
	
	
	
	public  void filldata()

	{
	    for (int j =0;j<32;j++)
	    {
	    	reg[j]=0;
	    	registerstatus[j]=1;
	    	checkarray[j]=1;
	    	checkarray2[j]=1;
	    }
	}
	
	void InstructionFetch ()
	{       
		if (ExecutedInstr.size()==1)
		{
			ExecutedInstr.remove(0);
		}
		if (WaitingInstr.size() == 1)
		{
			String a = WaitingInstr.get(0);
			String part [] = a.split(" ");
			if (part[0].equals("BEQ"))
			{
					part[1] = part[1].substring(1, part[1].length()-1);
				    part[2] = part[2].substring(1, part[2].length()-1);
				    part[3] = part[3].substring(1, part[3].length());
				    
				    int s1 = Integer.parseInt(part[1]);
				    int s2 = Integer.parseInt(part[2]);
				    if (registerstatus[s1] == 1 && registerstatus[s2] == 1)
				    { 
					    if(reg[s1] == reg[s2])
						{ 
					    	int g = Integer.parseInt(part[3]);
				    	    counter = counter +g;
					    	ExecutedInstr.add(a);
					    	WaitingInstr.remove(0);	
					    	stalled = false;
					    	counter = counter +4;
						}
					    else 
					    {    ExecutedInstr.add(a);
				    	     WaitingInstr.remove(0);
				    	     stalled = false;
					    	counter = counter +4;
					    }
				    }
			 }
			else if (part[0].equals("BGTZ"))
			{  
				   part[1] = part[1].substring(1, part[1].length()-1);
				   part[2] = part[2].substring(1, part[2].length());
				   int src = Integer.parseInt(part[1]);
				   
				   if (registerstatus[src] == 1)
				   {
					   if (reg[src] > 0)
					   {   
						   int k = Integer.parseInt(part[2]);
						   counter = counter +k;
						   ExecutedInstr.add(a);
					       WaitingInstr.remove(0);
					       stalled = false;
					       counter = counter +4;
					   }
					   else 
					    {    ExecutedInstr.add(a);
			    	         WaitingInstr.remove(0);
			    	         stalled = false;
					    	counter = counter +4;
					    }
				   }	
			}
			
		}
		else 
		{
		    if (stalled == false && PREISSUEUP.size()<4)
	        {
		    count = Integer.toString(counter);
			instruction1 = map2.get(count);
			parts1 = instruction1.split(" ");
			if (!parts1[0].equals("BEQ") && !parts1[0].equals("BGTZ") && !parts1[0].equals("J"))
				{   parts1[1] = parts1[1].substring(1, parts1[1].length()-1);
					int dest = Integer.parseInt(parts1[1]);
					registerstatus[dest]=0;
					br=false;
				}
			else br = true;
			
			
			counter = counter+4;
			count = Integer.toString(counter);
			instruction2 = map2.get(count);
			parts2 = instruction2.split(" ");
			
			if (!parts2[0].equals("BEQ") && !parts2[0].equals("BGTZ") && !parts2[0].equals("J") && br==false)
			{   parts2[1] = parts2[1].substring(1, parts2[1].length()-1);
				int dest = Integer.parseInt(parts2[1]);
				registerstatus[dest]=0;
			}
			
	        
            if (!(parts1[0].equals("J"))&&!(parts1[0].equals("BEQ"))&&!(parts1[0].equals("BGTZ")))
			{
				PREISSUEUP.add(instruction1);
			}
			
			else if ( ((parts1[0].equals("J")) || (parts1[0].equals("BEQ")) || 
					(parts1[0].equals("BGTZ"))) )
			{
				discarded = true;
				if (parts1[0].equals("BEQ"))
				{
					parts1[1] = parts1[1].substring(1, parts1[1].length()-1);
				    parts1[2] = parts1[2].substring(1, parts1[2].length()-1);
				    parts1[3] = parts1[3].substring(1, parts1[3].length());
				    
				    int src1 = Integer.parseInt(parts1[1]);
				    int src2 = Integer.parseInt(parts1[2]);
				    if (registerstatus[src1] == 1 && registerstatus[src2] == 1)
				    { 
					    if(reg[src1] == reg[src2])
						{   
					    	    int g = Integer.parseInt(parts1[3]);
					    	    counter = counter +g;
					    	    ExecutedInstr.add(instruction1);
					    	    waiting = false;
						}
					    else 
					    {   
					    	    ExecutedInstr.add(instruction1);
					    }
				    }
				    else 
					{ 
					    	WaitingInstr.add(instruction1);
					    	stalled = true;
					    	waiting = true;
					    	counter = counter-4;
				    }
			    }
				else if (parts1[0].equals("BGTZ"))
				{
					   parts1[1] = parts1[1].substring(1, parts1[1].length()-1);
					   parts1[2] = parts1[2].substring(1, parts1[2].length());
					   int src = Integer.parseInt(parts1[1]);
					   
					   if (registerstatus[src] == 1)
					   {
						   if (reg[src] > 0)
						   {   
							   int k = Integer.parseInt(parts1[2]);
							   counter = counter +k;
							   ExecutedInstr.add(instruction1);
							   waiting = false;
						   }
						   else 
						   {
							   ExecutedInstr.add(instruction1);
						   }
					   }
						
					   else 
					   {
						    	WaitingInstr.add(instruction1);
						    	stalled = true;
						    	waiting = true;
						    	counter = counter-4;
					   }
				}
				else if (parts1[0].equals("J"))
				{
								parts1[1] = parts1[1].substring(1, parts1[1].length());
							    int f = Integer.parseInt(parts1[1]);
							    counter = f-4;
							    ExecutedInstr.add(instruction1);
							    waiting = false;		    
				}
			}
			
			
			if ( ((parts2[0].equals("J")) || (parts2[0].equals("BEQ")) || (parts2[0].equals("BGTZ"))) 
					 && discarded == false)
			{ 
				if (parts2[0].equals("BEQ"))
				{
					parts2[1] = parts2[1].substring(1, parts2[1].length()-1);
				    parts2[2] = parts2[2].substring(1, parts2[2].length()-1);
				    parts2[3] = parts2[3].substring(1, parts2[3].length());
				    
				    int src1 = Integer.parseInt(parts2[1]);
				    int src2 = Integer.parseInt(parts2[2]);
				    if (registerstatus[src1] == 1 && registerstatus[src2] == 1)
				    { 
					    if(reg[src1] == reg[src2])
						{   
					    	    int g = Integer.parseInt(parts2[3]);
					    	    counter = counter +g;
					    	    ExecutedInstr.add(instruction2);
						}
					    else 
					    {   
					    	    ExecutedInstr.add(instruction2);
					    }
				    }
				    else 
					    { 
					    	WaitingInstr.add(instruction2);
					    	stalled = true;
					    }
			    }
				
				else if (parts2[0].equals("BGTZ"))
				{
					   parts2[1] = parts2[1].substring(1, parts2[1].length()-1);
					   parts2[2] = parts2[2].substring(1, parts2[2].length());
					   int src = Integer.parseInt(parts2[1]);
					   
					   if (registerstatus[src] == 1)
					   {
						   if (reg[src] > 0)
						   {   
							   int k = Integer.parseInt(parts1[2]);
							   counter = counter +k;
							   ExecutedInstr.add(instruction2);
						   }
						   else 
						   {
							   ExecutedInstr.add(instruction2);
						   }
					   }
						
					   else 
					   {
						    	WaitingInstr.add(instruction2);
						    	stalled = true;
					   }
				}
				
				else if (parts2[0].equals("J"))
				{
								parts2[1] = parts2[1].substring(1, parts2[1].length());
							    int f = Integer.parseInt(parts2[1]);
							    counter = f-4;
							    ExecutedInstr.add(instruction2);
							    
				}
	    	}
			
			else if (!(parts2[0].equals("J"))&&!(parts2[0].equals("BEQ"))&&!(parts2[0].equals("BGTZ"))
      	                && discarded == false)
			{
				PREISSUEUP.add(instruction2);
			}
			
			if (waiting == false && stalled == false)
			{
			counter = counter + 4; 
			}
	   }
		    else { }
			
	}
}
	
	boolean scoreboarding (String instruction)
	{
		
		return true;
	}
	
	void movetokens()
	{
		for (int j=0;j<PREISSUEUP.size();j++)
		  {
			  PREISSUEDOWN.add(PREISSUEUP.get(j)); 	
		  }
		for (int j=0;j<PREALUUP.size();j++)
		  {
			  PREALUDOWN.add(PREALUUP.get(j));
		  }
		for (int j=0;j<PREMEMUP.size();j++)
		  {
			  PREMEMDOWN.add(PREMEMUP.get(j)); 	
		  }
		for (int j=0;j<POSTMEMUP.size();j++)
		  {
			  POSTMEMDOWN.add(POSTMEMUP.get(j));
		  }
		for (int j=0;j<POSTALUUP.size();j++)
		  {
			  POSTALUDOWN.add(POSTALUUP.get(j)); 	
		  }
		
		 PREISSUEUP = new ArrayList<String>(4);
		 PREALUUP = new ArrayList<String>(2);
		 PREMEMUP = new ArrayList<String>(1);
		 POSTMEMUP = new ArrayList<String>(1);	
		 POSTALUUP = new ArrayList<String>(1);
		}
	
	void setdependency2 (String instruction)
	{
		String parts4 [] = instruction.split(" ");
		
		if (parts4[0].equals("ADD") || parts4[0].equals("SUB") || parts4[0].equals("MUL") || parts4[0].equals("OR") 
				|| parts4[0].equals("XOR") || parts4[0].equals("NOR"))
		{
			parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
			parts4[2] = parts4[2].substring(1, parts4[2].length()-1);
			parts4[3] = parts4[3].substring(1);
			
			dest = Integer.parseInt(parts4[1]);
			src1 = Integer.parseInt(parts4[2]); 
			src2 = Integer.parseInt(parts4[3]);
			checkarray2[src1] =0;
			checkarray2[src2] =0;
			
		}
		else if (parts4[0].equals("ADDI") || parts4[0].equals("ANDI") || parts4[0].equals("ORI") 
				|| parts4[0].equals("XORI"))
		{
			parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
			parts4[2] = parts4[2].substring(1, parts4[2].length()-1);
			parts4[3] = parts4[3].substring(1);
			
			dest = Integer.parseInt(parts4[1]);
			src1 = Integer.parseInt(parts4[2]); 
			checkarray2[src1] =0;
		}
		else if (parts4[0].equals("LW"))
		  {
			  parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
			  dest = Integer.parseInt(parts4[1]);
			  String third[] = parts4[2].split("\\(");
			  third[1] = third[1].substring(1, third[1].length()-1);
			  src1 = Integer.parseInt(third[1]);
			  checkarray2[src1] =0;
		  }
		else if (parts4[0].equals("SW"))
		{
			  parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
			  src1 = Integer.parseInt(parts4[1]);
			  String third[] = parts4[2].split("\\(");
			  third[1] = third[1].substring(1, third[1].length()-1);
			  src2 = Integer.parseInt(third[1]);
			  checkarray2[src1] =0;
			  checkarray2[src2] =0;
		} 		
	}

	void setdependency (String instruction)
	{
		String parts4 [] = instruction.split(" ");
		
		if (parts4[0].equals("ADD") || parts4[0].equals("SUB") || parts4[0].equals("MUL") || parts4[0].equals("OR") 
				|| parts4[0].equals("XOR") || parts4[0].equals("NOR"))
		{
			parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
			parts4[2] = parts4[2].substring(1, parts4[2].length()-1);
			parts4[3] = parts4[3].substring(1);
			
			dest = Integer.parseInt(parts4[1]);
			src1 = Integer.parseInt(parts4[2]); 
			src2 = Integer.parseInt(parts4[3]);
			
		}
		else if (parts4[0].equals("ADDI") || parts4[0].equals("ANDI") || parts4[0].equals("ORI") 
				|| parts4[0].equals("XORI"))
		{
			parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
			parts4[2] = parts4[2].substring(1, parts4[2].length()-1);
			parts4[3] = parts4[3].substring(1);
			
			dest = Integer.parseInt(parts4[1]);
			src1 = Integer.parseInt(parts4[2]); 
		}
		else if (parts4[0].equals("LW"))
		  {
			  parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
			  dest = Integer.parseInt(parts4[1]);
			  String third[] = parts4[2].split("\\(");
			  third[1] = third[1].substring(1, third[1].length()-1);
			  src1 = Integer.parseInt(third[1]);
		  }
		else if (parts4[0].equals("SW"))
		{
			  parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
			  src1 = Integer.parseInt(parts4[1]);
			  String third[] = parts4[2].split("\\(");
			  third[1] = third[1].substring(1, third[1].length()-1);
			  src2 = Integer.parseInt(third[1]);
		}
		
		checkarray[dest] = 0; 		
	}
	
	boolean checkdependency2 (String instruction)
	{
        String parts4 [] = instruction.split(" ");
		
		if (parts4[0].equals("ADD") || parts4[0].equals("SUB") || parts4[0].equals("MUL") || parts4[0].equals("OR") 
				|| parts4[0].equals("XOR") || parts4[0].equals("NOR"))
		{
			parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
			parts4[2] = parts4[2].substring(1, parts4[2].length()-1);
			parts4[3] = parts4[3].substring(1);
			
			dest = Integer.parseInt(parts4[1]);
			src1 = Integer.parseInt(parts4[2]); 
			src2 = Integer.parseInt(parts4[3]);
			
			if (checkarray2[dest] == 1)
			{
				return true;
			}
			else return false;
		}
		
		else if (parts4[0].equals("ADDI") || parts4[0].equals("ANDI") || parts4[0].equals("ORI") 
				|| parts4[0].equals("XORI"))
		{
			parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
			parts4[2] = parts4[2].substring(1, parts4[2].length()-1);
			parts4[3] = parts4[3].substring(1);
			
			dest = Integer.parseInt(parts4[1]);
			src1 = Integer.parseInt(parts4[2]); 
			if (checkarray2[dest] == 1)
			{
				return true;
			}
			else return false;
		}
		else if (parts4[0].equals("LW"))
		  {
			  parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
			  dest = Integer.parseInt(parts4[1]);
			  String third[] = parts4[2].split("\\(");
			  third[1] = third[1].substring(1, third[1].length()-1);
			  src1 = Integer.parseInt(third[1]);
			  if (checkarray2[dest] == 1)
				{
					return true;
				}
			  else return false;
		  }
		else if (parts4[0].equals("SW"))
		{
			  parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
			  src1 = Integer.parseInt(parts4[1]);
			  String third[] = parts4[2].split("\\(");
			  third[1] = third[1].substring(1, third[1].length()-1);
			  src2 = Integer.parseInt(third[1]);
			  return true;
		}
		return false;
		
	}

	boolean checkdependency (String instruction)
	{
        String parts4 [] = instruction.split(" ");
		
		if (parts4[0].equals("ADD") || parts4[0].equals("SUB") || parts4[0].equals("MUL") || parts4[0].equals("OR") 
				|| parts4[0].equals("XOR") || parts4[0].equals("NOR"))
		{
			parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
			parts4[2] = parts4[2].substring(1, parts4[2].length()-1);
			parts4[3] = parts4[3].substring(1);
			
			dest = Integer.parseInt(parts4[1]);
			src1 = Integer.parseInt(parts4[2]); 
			src2 = Integer.parseInt(parts4[3]);
			
			if (checkarray[src1] == 1 && checkarray[src2] ==1)
			{
				return true;
			}
			else return false;
		}
		
		else if (parts4[0].equals("ADDI") || parts4[0].equals("ANDI") || parts4[0].equals("ORI") 
				|| parts4[0].equals("XORI"))
		{
			parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
			parts4[2] = parts4[2].substring(1, parts4[2].length()-1);
			parts4[3] = parts4[3].substring(1);
			
			dest = Integer.parseInt(parts4[1]);
			src1 = Integer.parseInt(parts4[2]); 
			if (checkarray[src1] == 1)
			{
				return true;
			}
			else return false;
		}
		else if (parts4[0].equals("LW"))
		  {
			  parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
			  dest = Integer.parseInt(parts4[1]);
			  String third[] = parts4[2].split("\\(");
			  third[1] = third[1].substring(1, third[1].length()-1);
			  src1 = Integer.parseInt(third[1]);
			  if (checkarray[src1] == 1)
				{
					return true;
				}
			  else return false;
		  }
		else if (parts4[0].equals("SW"))
		{
			  parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
			  src1 = Integer.parseInt(parts4[1]);
			  String third[] = parts4[2].split("\\(");
			  third[1] = third[1].substring(1, third[1].length()-1);
			  src2 = Integer.parseInt(third[1]);
			  
			  if (checkarray[src1] == 1 && checkarray[src2] ==1)
				{
					return true;
				}
			  else return false;
		}
		return false;
		
	}
	
		void Issue ()
		{
			flag=0; flag2=0;
				if (PREALUUP.size()==2)
				{
					
			    } else  
			            {      
			    	       
			    	      while ( PREISSUEDOWN.size() > flag2 && flag < 2 && flag2 < 4) 
			                {
								if (PREISSUEDOWN.size() == 1 && PREALUUP.size()<2)
								{   
									if (scoreboarding(PREISSUEDOWN.get(flag2)) && checkdependency(PREISSUEDOWN.get(flag2)) 
											 /* && checkdependency2(PREISSUEDOWN.get(flag2)) */)
									{
									setdependency(PREISSUEDOWN.get(flag2));
									// setdependency2(PREISSUEDOWN.get(flag2));
									PREALUUP.add(PREISSUEDOWN.get(flag2));
									PREISSUEDOWN.remove(flag2);
									flag++;
									break;
									}
									
								}
								if (PREISSUEDOWN.size() >=2 && PREALUUP.size()==0)
								{
									if (scoreboarding(PREISSUEDOWN.get(flag2)) && checkdependency(PREISSUEDOWN.get(flag2))
											/* && checkdependency2(PREISSUEDOWN.get(flag2))*/ ) 
									{
									setdependency(PREISSUEDOWN.get(flag2));
									// setdependency2(PREISSUEDOWN.get(flag2));
									PREALUUP.add(PREISSUEDOWN.get(flag2));
									PREISSUEDOWN.remove(flag2); 
									flag2--;
									}
									
								}
								flag2++;
			                 }
				          }
				
		}
		
		void ALU ()
		{
			if (PREALUDOWN.size() > 0)
			{
				  String newinstruction = PREALUDOWN.get(0);
				  parts4 = newinstruction.split(" ");
				  if (parts4[0].equals("LW"))
				  {
					  parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
					  int des = Integer.parseInt(parts4[1]);
					  String third[] = parts4[2].split("\\(");
			
					  int off = Integer.parseInt(third[0]);
					  third[1] = third[1].substring(1, third[1].length()-1);
					  int bas = Integer.parseInt(third[1]);
					  
					  int key = reg[bas]+off;
					  String keystr = map3.get(Integer.toString(key));
		              // reg[des] =   Integer.parseInt(keystr);
		              String shortstring = newinstruction+ ":"+ "LW" + " "+ "R" + parts4[1] + "," + " "+ keystr; 
		              PREMEMUP.add (shortstring);
		              PREALUDOWN.remove(0);
				  }
				  else if (parts4[0].equals("SW"))
					{
					  parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
					  int des = Integer.parseInt(parts4[1]);
					  String third[] = parts4[2].split("\\(");
	
					  int off = Integer.parseInt(third[0]);
					  third[1] = third[1].substring(1, third[1].length()-1);
					  int bas = Integer.parseInt(third[1]);
					  
					  int key = reg[bas]+off;
					  /*String keystr = Integer.toString(key);
					  String valuestr = Integer.toString(reg[des]);
					  map3.put(keystr, valuestr);*/
					  String shortstring = newinstruction+ ":"+ "SW" + " "+ "R" + parts4[1] + "," + " "+ Integer.toString(key); 
					  PREMEMUP.add (shortstring);
		              PREALUDOWN.remove(0); 
					}
				  else if (parts4[0].equals("ADD"))
				{
					parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
					parts4[2] = parts4[2].substring(1, parts4[2].length()-1);
					parts4[3] = parts4[3].substring(1);
					
					int dest = Integer.parseInt(parts4[1]);
					int sum = reg[Integer.parseInt(parts4[2])] + reg [Integer.parseInt(parts4[3])];
					String shortstring = newinstruction+ ":"+"R" + parts4[1] + ", "+ Integer.toString(sum); 
					POSTALUUP.add (shortstring);
					PREALUDOWN.remove(0);
				}
				  else if (parts4[0].equals("SUB"))
				  {
					  
					parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
					parts4[2] = parts4[2].substring(1, parts4[2].length()-1);
					parts4[3] = parts4[3].substring(1);
					
                    int dest = Integer.parseInt(parts4[1]);
					int diff = reg[Integer.parseInt(parts4[2])] - reg [Integer.parseInt(parts4[3])];
					String shortstring = newinstruction+ ":"+"R" + parts4[1] + ", "+ Integer.toString(diff);
					
					POSTALUUP.add (shortstring);
					PREALUDOWN.remove(0);
				  }
				  
				  else if (parts4[0].equals("MUL"))
					{
						parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
						parts4[2] = parts4[2].substring(1, parts4[2].length()-1);
						parts4[3] = parts4[3].substring(1);
	                    
						int dest = Integer.parseInt(parts4[1]);
						int product = reg[Integer.parseInt(parts4[2])] * reg [Integer.parseInt(parts4[3])];
						String shortstring = newinstruction+ ":"+"R" + parts4[1] + ", "+ Integer.toString(product);
						
						POSTALUUP.add (shortstring);
						PREALUDOWN.remove(0);
					}
				  else if (parts4[0].equals("AND"))
					{
						parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
						parts4[2] = parts4[2].substring(1, parts4[2].length()-1);
						parts4[3] = parts4[3].substring(1);
						
						int dest = Integer.parseInt(parts4[1]);
						int and = reg[Integer.parseInt(parts4[2])] & reg [Integer.parseInt(parts4[3])];
                        
						String shortstring = newinstruction+ ":"+"R" + parts4[1] + ", "+ Integer.toString(and);
						POSTALUUP.add (shortstring);
						PREALUDOWN.remove(0);
					}
				  else if (parts4[0].equals("OR"))
					{
						parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
						parts4[2] = parts4[2].substring(1, parts4[2].length()-1);
						parts4[3] = parts4[3].substring(1);
						
						int dest = Integer.parseInt(parts4[1]);
						int or  = reg[Integer.parseInt(parts4[2])] | reg [Integer.parseInt(parts4[3])];
                        
						String shortstring = newinstruction+ ":"+"R" + parts4[1] + ", "+ Integer.toString(or);
						POSTALUUP.add (shortstring);
						PREALUDOWN.remove(0);
					}
				  else if (parts4[0].equals("XOR"))
					{
						parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
						parts4[2] = parts4[2].substring(1, parts4[2].length()-1);
						parts4[3] = parts4[3].substring(1);
						
						int dest = Integer.parseInt(parts4[1]);
						int xor = reg[Integer.parseInt(parts4[2])] ^ reg [Integer.parseInt(parts4[3])];
						
						String shortstring = newinstruction+ ":"+"R" + parts4[1] + ", "+ Integer.toString(xor);
						POSTALUUP.add (shortstring);
						PREALUDOWN.remove(0);	
					}
				  else if (parts4[0].equals("NOR"))
					{
						parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
						parts4[2] = parts4[2].substring(1, parts4[2].length()-1);
						parts4[3] = parts4[3].substring(1);
						
						int dest = Integer.parseInt(parts4[1]);
					    int nor = ~(reg[Integer.parseInt(parts4[2])] | reg [Integer.parseInt(parts4[3])]);
					    
					    String shortstring = newinstruction+ ":"+"R" + parts4[1] + ", "+ Integer.toString(nor);
						POSTALUUP.add (shortstring);
						PREALUDOWN.remove(0);
						
					}
				else if (parts4[0].equals("ADDI"))
					{ 
						parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
						parts4[2] = parts4[2].substring(1, parts4[2].length()-1);
						parts4[3] = parts4[3].substring(1);
						
						int dest = Integer.parseInt(parts4[1]);
						int addi = reg[Integer.parseInt(parts4[2])] + Integer.parseInt(parts4[3]);
						
						String shortstring = newinstruction+ ":"+"R" + parts4[1] + ", "+ Integer.toString(addi);
						POSTALUUP.add (shortstring);
						PREALUDOWN.remove(0);
					}
				else if (parts4[0].equals("ANDI"))
					{
						parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
						parts4[2] = parts4[2].substring(1, parts4[2].length()-1);
						parts4[3] = parts4[3].substring(1);
						
						int dest = Integer.parseInt(parts4[1]);
						int andi = reg[Integer.parseInt(parts4[2])] & Integer.parseInt(parts4[3]);
						
						String shortstring = newinstruction+ ":"+"R" + parts4[1] + ", "+ Integer.toString(andi);
						POSTALUUP.add (shortstring);
						PREALUDOWN.remove(0);
					}
				else if (parts4[0].equals("ORI"))
					{
						parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
						parts4[2] = parts4[2].substring(1, parts4[2].length()-1);
						parts4[3] = parts4[3].substring(1);
						
						int dest = Integer.parseInt(parts4[1]);
						int ori = reg[Integer.parseInt(parts4[2])] | Integer.parseInt(parts4[3]);
						
						String shortstring = newinstruction+ ":"+"R" + parts4[1] + ", "+ Integer.toString(ori);
						POSTALUUP.add (shortstring);
						PREALUDOWN.remove(0);
					}
				else if (parts4[0].equals("XORI"))
					{
						parts4[1] = parts4[1].substring(1, parts4[1].length()-1);
						parts4[2] = parts4[2].substring(1, parts4[2].length()-1);
						parts4[3] = parts4[3].substring(1);
						
						int dest = Integer.parseInt(parts4[1]);
						int xori = reg[Integer.parseInt(parts4[2])] ^ Integer.parseInt(parts4[3]);
						
						String shortstring = newinstruction+ ":"+"R" + parts4[1] + ", "+ Integer.toString(xori);
						POSTALUUP.add (shortstring);
						PREALUDOWN.remove(0);
					}	
			}
		}
		
		
		void MEM ()
		{
			if (PREMEMDOWN.size()>0)
			{
				String instruction = PREMEMDOWN.get(0);
				String newparts[] = instruction.split(":");
				String there[] = newparts[1].split(" ");
				String here[] = newparts[0].split(" ");
				if (here[0].equals("LW"))
				{
					POSTMEMUP.add(instruction);
					PREMEMDOWN.remove(0);
				}
				else if (here[0].equals("SW"))
				{
					here[1] = here[1].substring(1, here[1].length()-1);
					int des = Integer.parseInt(here[1]);
					String valuestr = Integer.toString(reg[des]);
					map3.put(there[2], valuestr);
					PREMEMDOWN.remove(0);
				}
			}
		}
		
		void WriteBack ()

		{
			 if (POSTALUDOWN.size()>0)
			 {
				 String instruction = POSTALUDOWN.get(0); 
				 String newparts [] = instruction.split(":");
				 String mynewparts[] = newparts[1].split(" ");
				 mynewparts[0] = mynewparts[0].substring(1, mynewparts[0].length()-1);
				 int r = Integer.parseInt(mynewparts[0]);
				 int result = Integer.parseInt(mynewparts[1]);
				 reg[r] = result;
				 registerstatus[r]=1;
				 checkarray[r]=1;
				 POSTALUDOWN.remove(0);
				 
				 String g[] = newparts[0].split(" ");
						 if (g[0].equals("ADD") || g[0].equals("SUB") || g[0].equals("MUL") || g[0].equals("OR") 
									|| g[0].equals("XOR") || g[0].equals("NOR"))
						 {
							    g[1] = g[1].substring(1,g[1].length()-1);
								g[2] = g[2].substring(1, g[2].length()-1);
								g[3] = g[3].substring(1);
								
								dest = Integer.parseInt(g[1]);
								src1 = Integer.parseInt(g[2]); 
								src2 = Integer.parseInt(g[3]);
								checkarray2[src1]=1;
								checkarray2[src2]=1;
						 }
						 else if (g[0].equals("ADDI") || g[0].equals("ANDI") || g[0].equals("ORI") 
									|| g[0].equals("XORI"))
						 {
							    g[1] = g[1].substring(1, g[1].length()-1);
								g[2] = g[2].substring(1, g[2].length()-1);
								g[3] = g[3].substring(1);
								
								dest = Integer.parseInt(g[1]);
								src1 = Integer.parseInt(g[2]); 
								checkarray2[src1]=1;
						 }
			 }
			 if (POSTMEMDOWN.size()>0)
			 {   
				 String instruction = POSTMEMDOWN.get(0); 
				 String newparts [] = instruction.split(":");
				 
				 String mynewparts[] = newparts[0].split(" ");
				 String third[] = mynewparts[2].split("\\(");
				 third[1] = third[1].substring(1, third[1].length()-1);
				 int src1 = Integer.parseInt(third[1]);
				 
				 newparts = newparts[1].split(" ");
				 newparts[1] = newparts[1].substring(1, newparts[1].length()-1);
				 int r = Integer.parseInt(newparts[1]);
				 int result = Integer.parseInt(newparts[2]);
				 reg[r] = result;
				 registerstatus[r]=1;
				 checkarray[r]=1;
				 checkarray2[src1]=1;
				 POSTMEMDOWN.remove(0);
			 }
		}

		void controller () throws FileNotFoundException
		{   
			int cycle=1;
		    // InstructionFetch();
		    
		    
			while (!(map2.get( Integer.toString(counter) ).equals("BREAK"))) 
			{
				InstructionFetch();
				Issue();
				ALU();
				MEM();
				WriteBack();
				movetokens();
				output2.append("--------------------");
				output2.append('\n');
				output2.append("Cycle:"+Integer.toString(cycle));
				output2.append('\n');output2.append('\n');
				output2.append("IF Unit:");
				output2.append('\n');
				output2.append('\t');
				output2.append("Waiting Instruction:");
				for (int j=0; j<WaitingInstr.size(); j++)
				{
					output2.append("["+WaitingInstr.get(j)+"]");
				}
				output2.append('\n');
				output2.append('\t');
				output2.append("Executed Instruction:");
				for (int j=0; j<ExecutedInstr.size(); j++)
				{
					output2.append("["+ExecutedInstr.get(j)+"]");
				}
				output2.append('\n');
				output2.append("Pre-Issue Queue:");
				output2.append('\n');
				for (int j=0; j<PREISSUEDOWN.size(); j++)
				{   
					output2.append('\t');
					output2.append("Entry "+ Integer.toString(j)+":"+ "["+PREISSUEDOWN.get(j)+"]");
					output2.append('\n');
				}
				output2.append("Pre-ALU Queue:");output2.append('\n');
				for (int j=0; j<PREALUDOWN.size(); j++)
				{   
					output2.append('\t');
					output2.append("Entry "+ Integer.toString(j)+":"+ "["+PREALUDOWN.get(j)+"]");
					output2.append('\n');
				}
				output2.append("Pre-Mem Queue:");
				for (int j=0; j<PREMEMDOWN.size(); j++)
				{ 
					String abc []= PREMEMDOWN.get(j).split(":");
					output2.append("["+abc[0]+"]");
					output2.append('\n');
				}
				output2.append('\n');
				output2.append("Post-Mem Queue:");
				for (int j=0; j<POSTMEMDOWN.size(); j++)
				{   
					String abc []= POSTMEMDOWN.get(j).split(":");
					output2.append("["+abc[0]+"]");
					output2.append('\n');
				}
				output2.append('\n');
				output2.append("Post-ALU Queue:");
				for (int j=0; j<POSTALUDOWN.size(); j++)
				{   
					String abc []= POSTALUDOWN.get(j).split(":");
					output2.append("["+abc[0]+"]");
					output2.append('\n');
				}
				output2.append('\n');
				
				  String rg[] = new String[32];
				  for (int k = 0; k<32;k++)
				  {
				  rg[k] = Integer.toString(reg[k]);
				  }
				  output2.append('\n');
				  output2.append("Registers"); output2.append('\n');
				  output2.append("R00:"); output2.append('\t');
				  for (int k =0; k<8;k++) { output2.append(rg[k]); output2.append('\t'); }
				  output2.append('\n'); 
				  output2.append("R08:"); output2.append('\t');
				  for (int k =8; k<16;k++) { output2.append(rg[k]); output2.append('\t');}
				  output2.append('\n');
				  output2.append("R16:");  output2.append('\t');
				  for (int k =16; k<24;k++) { output2.append(rg[k]); output2.append('\t');}
				  output2.append('\n');
				  output2.append("R24:");  output2.append('\t');
				  for (int k =24; k<32;k++) { output2.append(rg[k]); output2.append('\t');}
				  
						
	 			  output2.append('\n');
	 			  output2.append('\n');
	 			  output2.append("Data"); output2.append('\n');
				  int intindex1 = intindex+4;
				  int var =0;
				  output2.append(Integer.toString(intindex1));
				  output2.append(":"); 
				  output2.append('\t');
				  while ( (map3.get(Integer.toString(intindex1))!=null))
				  { 
					  if (var!=8)
					  {
					  output2.append(map3.get(Integer.toString(intindex1))); output2.append('\t');
					  intindex1 = intindex1+4;
					  var++;
					  }
					  else { output2.append('\n');
					  output2.append(Integer.toString(intindex1));
					  output2.append(":"); 
					  output2.append('\t'); var =0;
					  }
					  
				   }
				    output2.append('\n');
					cycle++;
			}
			
			   PrintWriter out1 = new PrintWriter("simulation.txt");
			   out1.println(output2);
			   out1.close();
			   // System.out.print(output2);
			
		}

	void readsamplefile(String file) throws IOException {
		FileInputStream fstream = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String readstring = new String();
		String strLine;
		while ((strLine = br.readLine()) != null)   
		{
		  readstring = readstring + strLine;
		}
		br.close();
		decode(readstring);
		}
	public static int converBinaryStringttoDecimal(String p)
    {
	    double j=0;
	    for(int i=0;i<p.length();i++)
	    {
	        if(p.charAt(i)== '1')
	        {
	        	j=j+ Math.pow(2,p.length()-1-i);
	        }
	    }
	    return (int) j;
	}
	
	void  createhashmap (String file) throws IOException {
		FileInputStream fstream = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String readstring = new String();
		String strLine;
		int arr[] = new int[2];
		while ((strLine = br.readLine()) != null)   
		{ 
			
			if (map2.containsValue("BREAK"))
			{
				   break;
			}
			else {
				   int length = strLine.length();
				   String value = strLine.substring(37,length);
				   String key = strLine.substring(33,36);
				   map2.put(key, value);  
			     } 
			if (c==1)
			{
				String key = strLine.substring(33,36);
				counter = Integer.parseInt(key);
			}
			else { }
			c++;
		 }
		
		 newvalue = strLine.substring(37,strLine.length());
		 newkey = strLine.substring(33,36);
		 map3.put(newkey, newvalue);
		
		while ((strLine = br.readLine()) != null)
		{    
			 lines++;
			 newvalue = strLine.substring(37,strLine.length());
			 newkey = strLine.substring(33,36);
			 map3.put(newkey, newvalue); 
		}
		// System.out.println(lines);
		
		
		
		br.close();
		/* System.out.println("PRINTING");
		 for (Object key : map3.keySet()) {
    		System.out.println(map3.get(key));
    	  } 
		  
          for (Object key : map3.keySet()) {
    		System.out.println("Key : " + key.toString());
    	} */
		
		for (Entry<String, String> entry : map2.entrySet()) {
	        if ("BREAK".equals(entry.getValue())) {
	             index = entry.getKey();
	        }
		}
		
		intindex = Integer.parseInt(index);
		
		
   }
	
	void printhashmap ()
	{   
		counter2 = counter;
		count = Integer.toString(counter2);
		while (!(map2.get(count).equals("BREAK")))
		{
			System.out.println(map2.get(count));
			counter2 +=4;
			count = Integer.toString(counter2);	
		}
	}
	
    public void decode(String readstr) throws IOException

	{
       int i=0;
       int d=128;
 	   
 	   String source1  = new String();
 	   String source2 = new String();
 	   String destination = new String();
 	   String opcode = new String();
 	   while ( i < readstr.length())
 	   {     
 		   String orig = readstr.substring(i, i+32);
 		   String str1= readstr.substring(i,i+3);
 		   String vald= Integer.toString(d);
 	       if (str1.equals("110"))
 		   {   
 			   int j = i+3;
 			   source1 = readstr.substring(j,j+5);
 			   int s1 =  converBinaryStringttoDecimal(source1);
 			   String ss1 = Integer.toString(s1);
 			   j=j+5;
 			   
 			   source2 = readstr.substring(j,j+5);
 			   int s2 =  converBinaryStringttoDecimal(source2);
 			   String ss2 = Integer.toString(s2);
 			   j=j+5;
 			   
 			   opcode = readstr.substring(j,j+3);
 			   j=j+3;
 			   
 			   destination = readstr.substring(j,j+5);
 			   int des = converBinaryStringttoDecimal(destination);
 			   String dest = Integer.toString(des);
 			   
 			   if (opcode.equals("000"))
 			   {
 				   output.append(orig);
 				   output.append('\t');
 				   output.append(vald);
 				   output.append('\t');
 				   output.append("ADD");
 				   output.append(" ");
 				   output.append("R"); output.append(dest); output.append(",");
 				   output.append(" ");
 				   output.append("R"); output.append(ss1); output.append(",");
 				   output.append(" ");
 				   output.append("R"); output.append(ss2);
 			   }
 			   else if (opcode.equals("001"))
 			   {
 				   output.append(orig);
 				   output.append('\t');
				   output.append(vald);
				   output.append('\t');
				   output.append("SUB");
				   output.append(" ");
				   output.append("R"); output.append(dest); output.append(",");
				   output.append(" ");
				   output.append("R"); output.append(ss1); output.append(",");
				   output.append(" ");
				   output.append("R"); output.append(ss2);
 			   }
 			   else if (opcode.equals("010"))
			   {
 				   output.append(orig);
 				   output.append('\t'); 
				   output.append(vald);
				   output.append('\t');
				   output.append("MUL");
				   output.append(" ");
				   output.append("R"); output.append(dest); output.append(",");
				   output.append(" ");
				   output.append("R"); output.append(ss1); output.append(",");
				   output.append(" ");
				   output.append("R"); output.append(ss2);
			   }
 			   else if (opcode.equals("011"))
			   {
 				   output.append(orig);
 				   output.append('\t');
				   output.append(vald);
				   output.append('\t');
				   output.append("AND");
				   output.append(" ");
				   output.append("R"); output.append(dest); output.append(",");
				   output.append(" ");
				   output.append("R"); output.append(ss1); output.append(",");
				   output.append(" ");
				   output.append("R"); output.append(ss2);
			   }
 			   else if (opcode.equals("100"))
			   {
 				   output.append(orig);
 				   output.append('\t');
				   output.append(vald);
				   output.append('\t');
				   output.append("OR");
				   output.append(" ");
				   output.append("R"); output.append(dest); output.append(",");
				   output.append(" ");
				   output.append("R"); output.append(ss1); output.append(",");
				   output.append(" ");
				   output.append("R"); output.append(ss2);	   
			   }
 			   else if (opcode.equals("101"))
			   {
 				   output.append(orig);
 				   output.append('\t');
				   output.append(vald);
				   output.append('\t');
				   output.append("XOR");
				   output.append(" ");
				   output.append("R"); output.append(dest); output.append(",");
				   output.append(" ");
				   output.append("R"); output.append(ss1); output.append(",");
				   output.append(" ");
				   output.append("R"); output.append(ss2);
			   }
 			   else if (opcode.equals("110"))
			   {
 				   output.append(orig);
 				   output.append('\t');
				   output.append(vald);
				   output.append('\t');
				   output.append("NOR");
				   output.append(" ");
				   output.append("R"); output.append(dest); output.append(",");
				   output.append(" ");
				   output.append("R"); output.append(ss1); output.append(",");
				   output.append(" ");
				   output.append("R"); output.append(ss2);
			   }
 		   }
 	           else  if (str1.equals("000"))
 	           {
 	        	   int j = i+3;
	 			   opcode = readstr.substring(j,j+3);
	 			   j=j+3;
	 			   
	 			   String instrin = readstr.substring(j,j+26);
	 			   int index =  converBinaryStringttoDecimal(instrin);
	 			   String indexstr = Integer.toString(index*4);
	 			   
	 			   String base = readstr.substring(j,j+5);
	 			   int ba =  converBinaryStringttoDecimal(base);
	 			   String bas = Integer.toString(ba);
	 			   j=j+5;
	 			   
	 			   destination = readstr.substring(j,j+5);
	 			   int des = converBinaryStringttoDecimal(destination);
	 			   String dest = Integer.toString(des);
	 			   j=j+5;
	 			   
	 			   String offset = readstr.substring(j,j+16);
	 			   int off = converBinaryStringttoDecimal(offset);
	 			   String offs = Integer.toString(off); 
	 			   String offsb = Integer.toString(off*4);
	 			   
	 			   
	 			   
	 			   if (opcode.equals("000"))
	 			   {
	 				  output.append(orig);
	 				  output.append('\t');
					  output.append(vald);
					  output.append('\t');
					  output.append("J"); output.append(" ");
					  output.append("#");output.append(indexstr);
	 				   
	 			   }
	 			   else  if (opcode.equals("010"))
	 			   {
	 				  output.append(orig);
	 				  output.append('\t');
					  output.append(vald);
					  output.append('\t');
					  output.append("BEQ");
					  output.append(" ");
					  output.append("R"); output.append(bas); output.append(",");
					  output.append(" ");
					  output.append("R"); output.append(dest); output.append(","); output.append(" ");
					  output.append("#"); output.append(offsb);  
	 			   }
	 			   else  if (opcode.equals("100"))
	 			   {
	 				  output.append(orig);
	 				  output.append('\t');
					  output.append(vald);
					  output.append('\t');
					  output.append("BGTZ");
					  output.append(" ");
					  output.append("R"); output.append(bas); output.append(",");
					  output.append(" ");
					  output.append("#"); output.append(offsb); 
	 			   }
	 			   else  if (opcode.equals("101"))
	 			   {
	 				   
	 				   output.append(orig);
	 				   output.append('\t');
					   output.append(vald);
					   output.append('\t');
	 				   output.append("BREAK");
	 				   output.append('\n');
	 				   d = d+4;
	 				   i = i+32;
	 				   break;
	 				}
	 			   else  if (opcode.equals("110"))
	 			   {
	 				  output.append(orig);
	 				  output.append('\t');
					  output.append(vald);
					  output.append('\t');
					  output.append("SW");
					  output.append(" ");
					  output.append("R"); output.append(dest); output.append(",");
					  output.append(" ");
					  output.append(offs); output.append("("); output.append("R"); output.append(bas);output.append(")");
	 			   }
	 			   else  if (opcode.equals("111"))
	 			   {
	 				  output.append(orig);
	 				  output.append('\t');
					  output.append(vald);
					  output.append('\t');
					  output.append("LW");
					  output.append(" ");
					  output.append("R"); output.append(dest); output.append(",");
					  output.append(" ");
					  output.append(offs); output.append("("); output.append("R"); output.append(bas);output.append(")");
				   }
 	        	   
 	           }
 	       
 	           else if(str1.equals("111"))
 	           {
 	        	   int j = i+3;
 	 			   source1 = readstr.substring(j,j+5);
 	 			   int s1 =  converBinaryStringttoDecimal(source1);
 	 			   String ss1 = Integer.toString(s1);
 	 			   j=j+5;
 	 			   
 	 			   destination = readstr.substring(j,j+5);
 	 			   int des =  converBinaryStringttoDecimal(destination);
 	 			   String dest = Integer.toString(des);
 	 			   j=j+5;
 	 			   
 	 			   opcode = readstr.substring(j,j+3);
 	 			   j=j+3;
 	 			   
 	 			   source2 = readstr.substring(j,j+16);
 	 			   int s2 = converBinaryStringttoDecimal(source2);
 	 			   String ss2 = Integer.toString(s2);
 	 			   
 	 			   if (opcode.equals("000"))
 	 			   {
 	 				 output.append(orig);
 	 				 output.append('\t');
 					 output.append(vald);
 					 output.append('\t');
 					 output.append("ADDI");
 					 output.append(" "); 
 					 output.append("R"); output.append(dest); output.append(",");
 					 output.append(" ");
 					 output.append("R"); output.append(ss1); output.append(",");
 					 output.append(" ");
 					 output.append("#"); output.append(ss2);
 	 			   }
 	 			   else if (opcode.equals("001"))
 	 			   {
 	 				   
 	 				 output.append(orig);
 	 				 output.append('\t');
 					 output.append(vald);
 					 output.append('\t');
 					 output.append("ANDI");
 					 output.append(" ");
 					 output.append("R"); output.append(dest); output.append(",");
 					 output.append(" ");
 					 output.append("R"); output.append(ss1); output.append(",");
 					 output.append(" ");
 					 output.append("#"); output.append(ss2);
 	 			   }
 	 			   else if (opcode.equals("010"))
 	 			   {
 	 				 output.append(orig);
 	 				 output.append('\t');
 					 output.append(vald);
 					 output.append('\t');
 					 output.append("ORI");
 					 output.append(" ");
 					 output.append("R"); output.append(dest); output.append(",");
 					 output.append(" ");
 					 output.append("R"); output.append(ss1); output.append(",");
 					 output.append(" ");
 					 output.append("#"); output.append(ss2);
 	 			   }
 	 			   else if (opcode.equals("011"))
 	 			   {
 	 				 output.append(orig);
 	 				 output.append('\t');
 					 output.append(vald);
 					 output.append('\t');
 					 output.append("XORI");
 					 output.append(" ");
 					 output.append("R"); output.append(dest); output.append(",");
 					 output.append(" ");
 					 output.append("R"); output.append(ss1); output.append(",");
 					 output.append(" ");
 					 output.append("#"); output.append(ss2);
 	 			   }
 	 			   
 	           }
 	    output.append('\n');
 	    d= d+4;
 	    i=i+32;
     }
 	   
 	   while ( i+32 < readstr.length())
 	   {
 		   String str1 = new String();
 		   str1 = readstr.substring(i, i+32);
 		   char c[] = str1.toCharArray();
 		   String vald = Integer.toString(d);
 		   if (c[0]=='1')
 		   {
	 		   for (int j=0; j < 32; j++)
	 		   {
		 			   if (c[j]== '0')
		 			   {
		 				   c[j]='1';
		 			   }
		 			   else if (c[j]== '1')
		 			   {
		 				  c[j]='0';
		 			   }
	 		   }
		 		   String a = String.valueOf(c);
				   int valu = converBinaryStringttoDecimal(a);
				   valu = valu+1;
				   String anew = String.valueOf(valu*-1);
				   output.append(str1);
				   output.append('\t');
				   output.append(vald);
				   output.append('\t');
				   output.append(anew);
				   output.append('\n');
	 		   }
 		   else 
 		   {
		 		   int valu = converBinaryStringttoDecimal(str1);
		 		   String anew = String.valueOf(valu);
		 		   output.append(str1);
		 		   output.append('\t');
				   output.append(vald);
				   output.append('\t');
				   output.append(anew);
		 		   output.append('\n');
 		   }
 		   
 		   d = d+4;
 		   i=i+32;
 		  
 	   } // end of while
 	   
 	   
 	   String str1 = new String();
	   str1 = readstr.substring(i, i+32);
	   char c[] = str1.toCharArray();
	   String vald = Integer.toString(d);
	   if (c[0]=='1')
	   {
		   for (int j=0; j < 32; j++)
		   {
	 			   if (c[j]== '0')
	 			   {
	 				   c[j]='1';
	 			   }
	 			   else if (c[j]== '1')
	 			   {
	 				  c[j]='0';
	 			   }
		   }
	 		   String a = String.valueOf(c);
			   int valu = converBinaryStringttoDecimal(a);
			   valu = valu+1;
			   String anew = String.valueOf(valu*-1);
			   output.append(str1);
			   output.append('\t');
			   output.append(vald);
			   output.append('\t');
			   output.append(anew);
		   }
	   else 
	   {
	 		   int valu = converBinaryStringttoDecimal(str1);
	 		   String anew = String.valueOf(valu);
	 		   output.append(str1);
	 		   output.append('\t');
			   output.append(vald);
			   output.append('\t');
			   output.append(anew);
	 		  
	   }
 	   
 	   
 	   // System.out.println(output);
 	   PrintWriter out = new PrintWriter("disassembly.txt");
	   out.println(output);
	   out.close();
 	   
	}

    public static void main(String args[]) throws IOException
	{ 
    	MIPSsim obj = new MIPSsim ();
		obj.readsamplefile(args[0]); 
		obj.createhashmap("disassembly.txt");
		// obj.printhashmap();
		obj.filldata();
		obj.controller();
		
		
	}


}