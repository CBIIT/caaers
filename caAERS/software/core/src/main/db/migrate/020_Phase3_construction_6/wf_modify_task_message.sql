update task_configuration set message = 'The task "Submit Reporting Period to AE Study Data Reviewer" is assigned to you. Please use the link ${REPORTING_PERIOD_LINK} to access the evaluation period.' where task_name='Submit Reporting Period for Data Coordinator Review';
update task_configuration set message = 'The task "AE Study Data Review" is assigned to you. Please use the link ${REPORTING_PERIOD_LINK} to access the evaluation period.' where task_name='Data Coordinator Review';
update task_configuration set message = 'The task "Provide Additional Information to AE Study Data Reviewer" is assigned to you. Please use the link ${REPORTING_PERIOD_LINK} to access the evaluation period.' where task_name='Provide Additional Information To Data Coordinator';
update task_configuration set message = 'The task "Submit Report to AE Expedited Report Reviewer" is assigned to you. Please use the link ${EXPEDITED_REPORT_LINK} to access the report.' where task_name='Submit Report To Central Office';
update task_configuration set message = 'The task "AE Expedited Report Review" is assigned to you. Please use the link ${EXPEDITED_REPORT_LINK} to access the report.' where task_name='Central Office Report Review';
update task_configuration set message = 'The task "Provide Additional Information to AE Expedited Report Reviewer" is assigned to you. Please use the link ${EXPEDITED_REPORT_LINK} to access the report.' where task_name='Provide Additional Information To Central Office';
