/*
 * jQuery File Upload Plugin JS Example 6.7
 * https://github.com/blueimp/jQuery-File-Upload
 *
 * Copyright 2010, Sebastian Tschan
 * https://blueimp.net
 *
 * Licensed under the MIT license:
 * http://www.opensource.org/licenses/MIT
 */

/*jslint nomen: true, unparam: true, regexp: true */
/*global $, window, document */

$(function () {
    'use strict';

   setInterval(chechStatus, 5000);


   function chechStatus(){
        $.getJSON('../logo/checkStatusJSON', function(data) {
            if (data.length>0){
                $('#tableProcess').empty();
                var table = document.getElementById("tableProcess");
                var rowCount = table.rows.length;
                $('#processListString').html("Processing List");
                var row = table.insertRow(rowCount);
                var cell1=row.insertCell(0);
                var cell2=row.insertCell(1);
                var cell3=row.insertCell(2);
                var cell4=row.insertCell(3);
                cell1.innerHTML="<h5>Logo</h5>";
                cell2.innerHTML="<h5>Video</h5>";
                cell3.innerHTML="<h5>Status</h5>";
                cell4.innerHTML="<h5>Detection result</h5>";
            }
            
            $.each(data, function(entryIndex, entry) {
                console.log("the entry: " + entry);
                 
                rowCount = table.rows.length;
                row = table.insertRow(rowCount);
                row.setAttribute('id',entry['idProcess']);
                cell1=row.insertCell(0);
                cell2=row.insertCell(1);
                cell3=row.insertCell(2);
                cell4=row.insertCell(3);
                
                var logoUrl = entry['logoUrl'].replace(/^.*[\\\/]/, '');
                var videoUrl = entry['videoUrl'].replace(/^.*[\\\/]/, '');
                cell1.innerHTML=logoUrl;
                cell2.innerHTML=videoUrl;
                cell3.innerHTML=entry['status'];
                cell4.innerHTML=entry['detection'];
                
             });
         }); 
   }
   
    
    $('#process_form').submit(function(e) {
        console.log("click");    
        var urls = [];
        $('input', $('.fileupload')).each(function(el) {
            if($(this).attr('type') == 'checkbox') {
               
                var c = $(this).get(0).checked;
                if(c == true) {
                    console.log($(this).parent().parent().find('.name a').attr('href'));
                    var url = $(this).parent().parent().find('.name a').attr('href');
                    urls.push(url);
                }
             } 
        });
        
        var urlsString = urls.join(",");
       
       
        $.post('../logo/process', {'urls': urlsString}, function(data) {
            console.log("You sent: " + data);
            chechStatus();
        });
        
        
        return false;
    });
    
    
    

    // Initialize the jQuery File Upload widget:
    //$('#fileupload').fileupload();


    $('.fileupload').each(function (index, obj) {
        var indice = parseInt(index+1);
        $(this).fileupload({
            dropZone: $(this),
            uploadTemplateId: "template-upload" + indice ,
            downloadTemplateId: "template-download" + indice,
            filesContainer: ".files" + indice,
            method : 'post',
            submit: function (e, data) {
                
                data.formData = {"form_type": indice};
             }
        });
    });
    
    // Enable iframe cross-domain access via redirect option:
    $('.fileupload').fileupload(
        'option',
        'redirect',
        window.location.href.replace(
            /\/[^\/]*$/,
            '/cors/result.html?%s'
        )
    );

    if (window.location.hostname === 'blueimp.github.com') {
        // Demo settings:
        $('.fileupload').fileupload('option', {
            
            url: '//jquery-file-upload.appspot.com/',
            maxFileSize: 5000000,
            acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i,
            process: [
                {
                    action: 'load',
                    fileTypes: /^image\/(gif|jpeg|png)$/,
                    maxFileSize: 20000000 // 20MB
                },
                {
                    action: 'resize',
                    maxWidth: 1440,
                    maxHeight: 900
                },
                {
                    action: 'save'
                }
            ]
        });
        // Upload server status check for browsers with CORS support:
        if ($.support.cors) {
            $.ajax({
                url: '//jquery-file-upload.appspot.com/',
                type: 'HEAD'
            }).fail(function () {
                $('<span class="alert alert-error"/>')
                    .text('Upload server currently unavailable - ' +
                            new Date())
                    .appendTo('.fileupload');
            });
        }
    } else {
        // Load existing files:
        $('.fileupload').each(function (index, object) {
            var that = this;
            $.getJSON(this.action,{'form_type': index+1}, function (result) {
                if (result && result.length) {
                    $(that).fileupload('option', 'done')
                        .call(that, null, {result: result});
                }
                
            });
        });
    }

});
