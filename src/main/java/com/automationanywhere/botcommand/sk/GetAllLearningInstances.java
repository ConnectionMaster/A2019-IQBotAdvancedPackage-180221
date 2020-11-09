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



import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.StringValue;

import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;

import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.Sessions;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import com.automationanywhere.commandsdk.model.DataType;
import com.automationanywhere.commandsdk.annotations.Execute;


/**
 * @author Stefan Karsten
 *
 */

@BotCommand
@CommandPkg(label = "All Learning Instances", name = "AllInstances",
        description = "All Learning Instances",
        node_label = "All Learning Instances {{sessionName}}|", icon = "pkg.svg",  group_label="Admin",
        return_type=DataType.DICTIONARY, return_sub_type = DataType.STRING , return_label="Dictionary of all Learning Instances", return_required=true)
public class GetAllLearningInstances {
	
	  private static final Logger logger = LogManager.getLogger(GetAllLearningInstances.class);
	
    @Sessions
    private Map<String, Object> sessions;
    
    private static final Messages MESSAGES = MessagesFactory
			.getMessages("com.automationanywhere.botcommand.demo.messages");
	
	   
	@Execute
    public DictionaryValue action(@Idx(index = "1", type = TEXT)  @Pkg(label = "Session name" , default_value_type = STRING , default_value = "Default") @NotEmpty String sessionName 
    		
    		) throws Exception
     {
		

	    IQBotConnection connection  = (IQBotConnection) this.sessions.get(sessionName);  
	    HashMap<String,String>li_map = Utils.getLearningInstances(connection);
	    
		HashMap<String,Value> map = new HashMap();
	    li_map.forEach((k,v)->{
	        map.put(k, new StringValue(v));
	    });
	   
	    DictionaryValue dictmap = new DictionaryValue();
	    dictmap.set(map);
	    return dictmap;
     
     }
	
	
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }
    
		
	
}