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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.ListValue;
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
@CommandPkg(label = "Get File Names", name = "NameofFiles",
        description = "Get File Names in a certain status",
        node_label = "Get File Names", icon = "pkg.svg",   group_label="Processing",
        return_type=DataType.LIST, return_sub_type = DataType.STRING , return_label="File Names", return_required=true)

public class GetFiles {
	
	  private static final Logger logger = LogManager.getLogger(GetFiles.class);
	
    @Sessions
    private Map<String, Object> sessions;
    
    private static final Messages MESSAGES = MessagesFactory
			.getMessages("com.automationanywhere.botcommand.demo.messages");
	
	   
	@Execute
    public ListValue action(@Idx(index = "1", type = TEXT)  @Pkg(label = "Session name" , default_value_type = STRING , default_value = "Default") @NotEmpty String sessionName ,
    		 				 @Idx(index = "2", type = TEXT)  @Pkg(label = "LI Name" , default_value_type = STRING) @NotEmpty String li_name ,
    						 @Idx(index = "3", type = SELECT, options = {
    								 	@Idx.Option(index = "3.1", pkg = @Pkg(label = "Success", value = "SUCCESS")),
    								 	@Idx.Option(index = "3.2", pkg = @Pkg(label = "Invalid", value = "INVALID")),
    								 	@Idx.Option(index = "3.3", pkg = @Pkg(label = "Unclassified", value = "UNCLASSIFIED")),
    								 	@Idx.Option(index = "3.4", pkg = @Pkg(label = "Untrained", value = "UNTRAINED")),
    								 	@Idx.Option(index = "3.5", pkg = @Pkg(label = "Validate", value = "VALIDATION"))}) 
    									@Pkg(label = "Document Type", default_value = "SUCCESS", default_value_type = STRING) @NotEmpty String doctype,
    						 @Idx(index = "4", type = AttributeType.BOOLEAN)  @Pkg(label = "Validate URL" , default_value_type = DataType.BOOLEAN, default_value="false" , description = "If Document Type = Validate then it returns Validator URLs") Boolean getURL) throws Exception
     {
		
		List<Value> files = new ArrayList<Value>();
		List<String> filelist = new ArrayList<String>();
		
		getURL = (getURL == null) ? false : getURL;

	    IQBotConnection connection  = (IQBotConnection) this.sessions.get(sessionName);  
	 
	    HashMap<String,String> lis = Utils.getLearningInstances(connection);
		   
	    if(lis.containsKey(li_name)) {
	    	 filelist  = Utils.getOutputFiles(connection,lis.get(li_name), doctype,getURL);
	    }

	    filelist.forEach(file->{
	        files.add(new StringValue(file));
	    });
	   
	    ListValue listval = new ListValue();
	    listval.set(files);
	   
	    return listval;
     
     }
	
	
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }
    
		
	
}