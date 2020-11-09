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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Row;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
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
@CommandPkg(label = "Format IQBot Result", name = "FormatResult",
        description = "Format IQBot Result",
        node_label = "Format IQBot Result", icon = "pkg.svg",    group_label="Processing",
        return_type=DataType.DICTIONARY, return_sub_type = DataType.ANY , return_label="Dictionary with Header and Positions", return_description = "Note: Dictionary with key 'header' and key 'positions'. 'header' is a dictonary , 'positions' a table." ,return_required=true)
public class FormatResult {
	
	  private static final Logger logger = LogManager.getLogger(FormatResult.class);
	
    private static final Messages MESSAGES = MessagesFactory
			.getMessages("com.automationanywhere.botcommand.demo.messages");
	
	   
	@Execute
    public DictionaryValue action( @Idx(index = "1", type = AttributeType.TABLE)  @Pkg(label = "IQBot Result" , default_value_type = DataType.TABLE) @NotEmpty Table iqtable ,
    							   @Idx(index = "2", type = AttributeType.NUMBER)  @Pkg(label = "Header Last Column" , default_value_type = DataType.NUMBER)  Number lastindex ) {
    		
	    HashMap<String, Value> outmap = new HashMap<String, Value>();
	    Table data = new Table();
	    HashMap<String, Value> header = new HashMap<String, Value>();
	    List<Row> rows = iqtable.getRows();
	    
	    int lastInd = (lastindex != null) ? lastindex.intValue() : 0;
	    
	    List<Value> columnnames = rows.get(0).getValues();
	    int noCols = columnnames.size();
	    
	    rows.remove(0);
	    int noRows = rows.size();
	    
	    Boolean[] equalcolumns = new Boolean[noCols];
	    int  index =0;
	    for (Value value : columnnames) {
	    	equalcolumns[index++] = true;		
		}

	    
	    List<Value> firstrowValues = rows.get(0).getValues();
	    
	    for (Row row : rows) {
	    	List<Value> values = row.getValues();
	    	for (int i = 0; i < values.size(); i++) {
		    	String valueStr1 = ((StringValue)firstrowValues.get(i)).toString();
	    		String valueStr2 = ((StringValue)values.get(i)).toString();
	    		if (!valueStr1.equals(valueStr2)) {
	    			equalcolumns[i] = false;
	    		}
	    		index++;
			}
		}
	    
	    List<Row> datarows = new ArrayList<Row>();
	    List<Schema> dataschema =  new ArrayList<Schema>();
	    for (Row row : rows) {
	    	List<Value> values = row.getValues();
	    	Row datarow = new Row();
	    	index = 0;
	    	List<Value> datavalues = new ArrayList<Value>();
	    	for (Value value : values) {
	    		String valueStr = ((StringValue)value).toString();

	    	    String colname = columnnames.get(index).toString().trim().replaceAll("\n", "").replaceAll("\r", "");
	    	    logger.info("Column name"+colname);
	    	    if  (lastInd == 0)
	    	    {
	    	    	if (equalcolumns[index]) {
	    	    		header.put(colname, new StringValue(valueStr));
	    	    	}
	    	    	else
	    	    	{
	    	    		datavalues.add(new StringValue(valueStr));
	    	    		Schema schemaexists = dataschema.stream()
	    	    				.filter(schema -> colname.equals(schema.getName()))
	    	    				.findAny()
	    	    				.orElse(null);
	    	    		if (schemaexists == null)
	    	    		{
	    	    			Schema schema = new Schema();
	    	    			schema.setName(colname);
	    	    			schema.setType(com.automationanywhere.botcore.api.dto.AttributeType.STRING);
	    	    			dataschema.add(schema);
	    	    		}
	    	    	}
	    	    }
	    	    else {
	    	    	if (index < lastInd ) {
	    	    		header.put(colname, new StringValue(valueStr));
	    	    	}
	    	    	else {
	    	    		datavalues.add(new StringValue(valueStr));
	    	    		Schema schemaexists = dataschema.stream()
    	    				.filter(schema -> colname.equals(schema.getName()))
    	    				.findAny()
    	    				.orElse(null);
	    	    		if (schemaexists == null)
	    	    		{
	    	    			Schema schema = new Schema();
	    	    			schema.setName(colname);
	    	    			schema.setType(com.automationanywhere.botcore.api.dto.AttributeType.STRING);
	    	    			dataschema.add(schema);
	    	    		}
	    	    		
	    	    	}
	    	    }
	    	    index++;
			}
	    	datarow.setValues(datavalues);
	    	datarows.add(datarow);
		}
	    
	    data.setSchema(dataschema);
	    data.setRows(datarows);

	        

	    DictionaryValue dictmap = new DictionaryValue();

	    outmap.put("header", new DictionaryValue( header));
		outmap.put("positions", new TableValue(data));
	    
		dictmap.set(outmap);
	    return dictmap;
     
     }
	
	
}