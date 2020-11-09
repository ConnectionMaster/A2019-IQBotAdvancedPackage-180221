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

import com.automationanywhere.botcommand.data.impl.NumberValue;


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
@CommandPkg(label = "Validations for a Learning Instance", name = "LIValidation",
        description = "Validations for a Learning Instance",  group_label="Processing",
        node_label = "Validations for a Learning Instance {{sessionName}}|", icon = "pkg.svg",
        return_type=DataType.NUMBER, return_label="No. of Validations", return_required=true)
public class GetValidationStatus {
	
	  private static final Logger logger = LogManager.getLogger(GetValidationStatus.class);
	
    @Sessions
    private Map<String, Object> sessions;
    
    private static final Messages MESSAGES = MessagesFactory
			.getMessages("com.automationanywhere.botcommand.demo.messages");
	
	   
	@Execute
    public NumberValue action(@Idx(index = "1", type = TEXT)  @Pkg(label = "Session name" , default_value_type = STRING , default_value = "Default") @NotEmpty String sessionName ,
    					      @Idx(index = "2", type = TEXT)  @Pkg(label = "LI Name" , default_value_type = STRING) @NotEmpty String li_name 
    		
    		) throws Exception
     {
		
		int validations = 0;

	    IQBotConnection connection  = (IQBotConnection) this.sessions.get(sessionName);  
	    
	    HashMap<String,String> lis = Utils.getLearningInstances(connection);
	   
	    if(lis.containsKey(li_name)) {
	    	validations = Utils.getValidationsinQueue(connection, lis.get(li_name)) ;
	    }
	    

		return new NumberValue(validations) ;
     
     }
	
	
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }
    
		
	
}