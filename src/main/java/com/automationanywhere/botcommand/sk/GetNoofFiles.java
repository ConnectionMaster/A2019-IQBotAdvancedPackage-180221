/*
 * Copyright (c) 2019 Automation Anywhere.
 * All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere.
 * You shall use it only in accordance with the terms of the license agreement
 * you entered into with Automation Anywhere.
 */
/**
 * 
 */
package com.automationanywhere.botcommand.sk;



import static com.automationanywhere.commandsdk.model.AttributeType.SELECT;
import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.botcommand.data.impl.StringValue;

import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;

import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.Sessions;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import com.automationanywhere.commandsdk.annotations.Execute;


/**
 * @author Stefan Karsten
 *
 */

@BotCommand
@CommandPkg(label = "No of Files", name = "NoOfFiles",
        description = "No of Files in a certain status",
        node_label = "No of Files", icon = "pkg.svg", group_label="Instance",
        return_type=DataType.NUMBER,  return_label="No of Files", return_required=true)
public class GetNoofFiles {
	
	  private static final Logger logger = LogManager.getLogger(GetNoofFiles.class);
	
    @Sessions
    private Map<String, Object> sessions;
    
    private static final Messages MESSAGES = MessagesFactory
			.getMessages("com.automationanywhere.botcommand.demo.messages");
	
	   
	@Execute
    public NumberValue action(@Idx(index = "1", type = TEXT)  @Pkg(label = "Session name" , default_value_type = STRING , default_value = "Default") @NotEmpty String sessionName ,
    		 				 @Idx(index = "2", type = TEXT)  @Pkg(label = "LI Name" , default_value_type = STRING) @NotEmpty String li_name ,
    						 @Idx(index = "3", type = SELECT, options = {
    								 	@Idx.Option(index = "3.1", pkg = @Pkg(label = "Success", value = "SUCCESS")),
    								 	@Idx.Option(index = "3.2", pkg = @Pkg(label = "Invalid", value = "INVALID")),
    								 	@Idx.Option(index = "3.3", pkg = @Pkg(label = "Unclassified", value = "UNCLASSIFIED")),
    								 	@Idx.Option(index = "3.4", pkg = @Pkg(label = "Untrained", value = "UNTRAINED")),
    								 	@Idx.Option(index = "3.5", pkg = @Pkg(label = "Validate", value = "VALIDATION"))}) 
    						            @Pkg(label = "Document Type", default_value = "SUCCESS", default_value_type = STRING) @NotEmpty String doctype
    						) throws Exception
     {
		
		int noofFiles = 0;

	    IQBotConnection connection  = (IQBotConnection) this.sessions.get(sessionName);  
	 
	    HashMap<String,String> lis = Utils.getLearningInstances(connection);
		   
	    if(lis.containsKey(li_name)) {
	    	 noofFiles  = Utils.getNofOfOutputFiles(connection,lis.get(li_name), doctype);
	    }
	   
	    return new NumberValue(noofFiles);
     
     }
	
	
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }
    
		
	
}