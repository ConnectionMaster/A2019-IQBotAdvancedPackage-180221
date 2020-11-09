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
@CommandPkg(label = "Download Documents", name = "DownloadDocument",
        description = "Download Documents in a certain status",
        node_label = "Download Document", icon = "pkg.svg",   group_label="Processing",
        return_type=DataType.STRING,  return_label="Download Status", return_required=false)

public class DownloadFiles {
	
	  private static final Logger logger = LogManager.getLogger(DownloadFiles.class);
	
    @Sessions
    private Map<String, Object> sessions;
    
    private static final Messages MESSAGES = MessagesFactory
			.getMessages("com.automationanywhere.botcommand.demo.messages");
	
	   
	@Execute
    public StringValue action(@Idx(index = "1", type = TEXT)  @Pkg(label = "Session name" , default_value_type = STRING , default_value = "Default") @NotEmpty String sessionName ,
    		 				 @Idx(index = "2", type = TEXT)  @Pkg(label = "LI Name" , default_value_type = STRING) @NotEmpty String li_name ,
							 @Idx(index = "3", type = TEXT)  @Pkg(label = "Directory Path" , default_value_type = STRING) @NotEmpty String filepath ,
    						 @Idx(index = "4", type = SELECT, options = {
    								 	@Idx.Option(index = "4.1", pkg = @Pkg(label = "Success", value = "SUCCESS")),
    								 	@Idx.Option(index = "4.2", pkg = @Pkg(label = "Invalid", value = "INVALID")),
    								 	@Idx.Option(index = "4.3", pkg = @Pkg(label = "Unclassified", value = "UNCLASSIFIED")),
    								 	@Idx.Option(index = "4.4", pkg = @Pkg(label = "Untrained", value = "UNTRAINED"))}) 
    									@Pkg(label = "Document Type", default_value = "SUCCESS", default_value_type = STRING) @NotEmpty String doctype,
    						 @Idx(index = "5", type = AttributeType.BOOLEAN)  @Pkg(label = "Delete Documents" , default_value_type = DataType.BOOLEAN, default_value="false" , description = "Delete files from the server after downloading") Boolean delete				
    		
    		) throws Exception
     {
		
		String status="";

	    IQBotConnection connection  = (IQBotConnection) this.sessions.get(sessionName);  
	 
	    HashMap<String,String> lis = Utils.getLearningInstances(connection);
		   
	    if(lis.containsKey(li_name)) {
	    	 status = Utils.downloadDocuments(connection, lis.get(li_name), doctype, filepath);
	    	 if (delete) {
	    		 List<String> filelist = Utils.getOutputFiles(connection,lis.get(li_name), doctype,false);
	    		 filelist.forEach(file->{
						String fileid = file.split("_")[0];
	    			 	try {
							Utils.deleteLIFile(connection, lis.get(li_name), fileid);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("DELETE FILE"+e.getMessage());
						}
	    		    });
	    		   
	    	
	    		}
	    }
	    return new StringValue(status);
     
     }
	
	
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }
    
		
	
}