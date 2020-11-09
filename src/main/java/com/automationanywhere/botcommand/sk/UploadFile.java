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
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.data.impl.RecordValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.record.Record;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;

import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.Sessions;
import com.automationanywhere.commandsdk.annotations.rules.FileExtension;
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
@CommandPkg(label = "Upload Document", name = "UploadDocument",
        description = "Upload Document to Learning Instance",
        node_label = "Upload Document", icon = "pkg.svg",   group_label="Processing",
        return_type=DataType.STRING,  return_label="Upload Status", return_required=false)

public class UploadFile {
	
	 private static final Logger logger = LogManager.getLogger(UploadFile.class);
	
    @Sessions
    private Map<String, Object> sessions;
    
    private static final Messages MESSAGES = MessagesFactory
			.getMessages("com.automationanywhere.botcommand.demo.messages");
	
	   
	@Execute
    public StringValue action(@Idx(index = "1", type = TEXT)  @Pkg(label = "Session name" , default_value_type = STRING , default_value = "Default") @NotEmpty String sessionName ,
			 				  @Idx(index = "2", type = TEXT)  @Pkg(label = "LI Name" , default_value_type = STRING) @NotEmpty String li_name ,
    					      @Idx(index = "3", type = AttributeType.FILE)  @Pkg(label = "Document Path" , default_value_type = DataType.FILE)  @NotEmpty String filename ) throws Exception
     {

		String status = "";
	    IQBotConnection connection  = (IQBotConnection) this.sessions.get(sessionName);  
	 
	    HashMap<String,String> lis = Utils.getLearningInstances(connection);
		   
	    if(lis.containsKey(li_name)) {
	    	 status = Utils.putDocument(connection, lis.get(li_name),filename);
	    }
	    return new StringValue(status);
     
     }
	
	
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }
    
		
	
}