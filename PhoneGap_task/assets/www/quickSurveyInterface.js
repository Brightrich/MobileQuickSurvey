function buildQuestionnairesTable(){

  
  //Get Feed rate
  var feedRate = window.PageAttr.getFeedbackRate();
  var feedRateArr = feedRate.split("#");

  //GUIDELINE
  //    0          1             2                     3
  //KEY_ROWID, APP_NAME, CRITERIA_ORDER_NUMBER, CRITERIA_DESC
  //delimited by @ for each fields and # for each row
  var quest = window.PageAttr.getQuestionnaireData();
  	
  var table = document.getElementById("table_quest");

	var questRow = quest.split("#");
	for(var x = 0; x<questRow.length;x++){		
		var questAttr = questRow[x].split("@");
		
			//APPEND ELEM HERE		
					 	//Add Questions Row		
            var qRow = table.insertRow(x*2);

            var qCell = qRow.insertCell(0);
            qCell.setAttribute("colspan", 5);
                        
            var qLabel = document.createElement("label");
            var qStrong = document.createElement("strong");
            qStrong.innerHTML = questAttr[3];

            qLabel.appendChild(qStrong);
            qCell.appendChild(qLabel);

            //Append emotes row
            var eRow = table.insertRow((x*2)+1);
                        
            //Iterate feedback Rate
            //GUIDELINE 
            //     0          1              2              3
            //{KEY_ROWID, RATE_NAME, RATE_DESCRIPTION, IMAGE_PATH}
            //delimited by @ for each fields and # for each row    

            for(var z=0; z < feedRateArr.length; z++){
              var feedRateCol = feedRateArr[z].split("@");

              var eCell = eRow.insertCell(z);
              eCell.style.textAlign = "center";

              var eInput = document.createElement("input");
              eInput.type = "image";

              //image_path
              //eInput.src = "emotes/"+(z+1)+".png";
              eInput.src = feedRateCol[3];
              eInput.setAttribute("height","50");
              eInput.setAttribute("width","50");
              
              //QUEST ATTR GUIDELINE
              //    0          1             2                     3
              //KEY_ROWID, APP_NAME, CRITERIA_ORDER_NUMBER, CRITERIA_DESC                    
              var idIpt = questAttr[0]+"#"+questAttr[1]+"#"+questAttr[2]+"#"+feedRateCol[0];
              
              eInput.id = idIpt;
              eInput.setAttribute("onClick", myFunction(idIpt));
              eCell.appendChild(eInput);

              //$("#idIpt").bind("click", function(){ myFunction(idIpt); });
            }	                     
	}	
	
	function myFunction(x){
      //KEY_ROWID, APP_NAME, CRITERIA_ORDER_NUMBER, RATE_ID
  		return "chosen('"+x+"')";
 	}
}