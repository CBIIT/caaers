delete from csm_user_group where user_id = -9;
delete from csm_user_group where user_id = -7;

insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'SYSTEM'),(select group_id from csm_group where group_name = 'system_administrator'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'SYSTEM'),(select group_id from csm_group where group_name = 'business_administrator'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'SYSTEM'),(select group_id from csm_group where group_name = 'person_and_organization_information_manager'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'SYSTEM'),(select group_id from csm_group where group_name = 'data_importer'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'SYSTEM'),(select group_id from csm_group where group_name = 'user_administrator'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'SYSTEM'),(select group_id from csm_group where group_name = 'study_qa_manager'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'SYSTEM'),(select group_id from csm_group where group_name = 'study_creator'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'SYSTEM'),(select group_id from csm_group where group_name = 'supplemental_study_information_manager'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'SYSTEM'),(select group_id from csm_group where group_name = 'study_team_administrator'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'SYSTEM'),(select group_id from csm_group where group_name = 'study_site_participation_administrator'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'SYSTEM'),(select group_id from csm_group where group_name = 'ae_rule_and_report_manager'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'SYSTEM'),(select group_id from csm_group where group_name = 'study_calendar_template_builder'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'SYSTEM'),(select group_id from csm_group where group_name = 'registration_qa_manager'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'SYSTEM'),(select group_id from csm_group where group_name = 'subject_manager'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'SYSTEM'),(select group_id from csm_group where group_name = 'study_subject_calendar_manager'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'SYSTEM'),(select group_id from csm_group where group_name = 'registrar'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'SYSTEM'),(select group_id from csm_group where group_name = 'ae_reporter'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'SYSTEM'),(select group_id from csm_group where group_name = 'expedited_report_reviewer'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'SYSTEM'),(select group_id from csm_group where group_name = 'adverse_event_study_data_reviewer'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'SYSTEM'),(select group_id from csm_group where group_name = 'lab_impact_calendar_notifier'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'SYSTEM'),(select group_id from csm_group where group_name = 'lab_data_user'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'SYSTEM'),(select group_id from csm_group where group_name = 'data_reader'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'SYSTEM'),(select group_id from csm_group where group_name = 'data_analyst'));

insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'cctsdemo1@nci.nih.gov'),(select group_id from csm_group where group_name = 'system_administrator'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'cctsdemo1@nci.nih.gov'),(select group_id from csm_group where group_name = 'business_administrator'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'cctsdemo1@nci.nih.gov'),(select group_id from csm_group where group_name = 'person_and_organization_information_manager'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'cctsdemo1@nci.nih.gov'),(select group_id from csm_group where group_name = 'data_importer'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'cctsdemo1@nci.nih.gov'),(select group_id from csm_group where group_name = 'user_administrator'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'cctsdemo1@nci.nih.gov'),(select group_id from csm_group where group_name = 'study_qa_manager'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'cctsdemo1@nci.nih.gov'),(select group_id from csm_group where group_name = 'study_creator'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'cctsdemo1@nci.nih.gov'),(select group_id from csm_group where group_name = 'supplemental_study_information_manager'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'cctsdemo1@nci.nih.gov'),(select group_id from csm_group where group_name = 'study_team_administrator'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'cctsdemo1@nci.nih.gov'),(select group_id from csm_group where group_name = 'study_site_participation_administrator'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'cctsdemo1@nci.nih.gov'),(select group_id from csm_group where group_name = 'ae_rule_and_report_manager'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'cctsdemo1@nci.nih.gov'),(select group_id from csm_group where group_name = 'study_calendar_template_builder'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'cctsdemo1@nci.nih.gov'),(select group_id from csm_group where group_name = 'registration_qa_manager'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'cctsdemo1@nci.nih.gov'),(select group_id from csm_group where group_name = 'subject_manager'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'cctsdemo1@nci.nih.gov'),(select group_id from csm_group where group_name = 'study_subject_calendar_manager'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'cctsdemo1@nci.nih.gov'),(select group_id from csm_group where group_name = 'registrar'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'cctsdemo1@nci.nih.gov'),(select group_id from csm_group where group_name = 'ae_reporter'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'cctsdemo1@nci.nih.gov'),(select group_id from csm_group where group_name = 'expedited_report_reviewer'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'cctsdemo1@nci.nih.gov'),(select group_id from csm_group where group_name = 'adverse_event_study_data_reviewer'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'cctsdemo1@nci.nih.gov'),(select group_id from csm_group where group_name = 'lab_impact_calendar_notifier'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'cctsdemo1@nci.nih.gov'),(select group_id from csm_group where group_name = 'lab_data_user'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'cctsdemo1@nci.nih.gov'),(select group_id from csm_group where group_name = 'data_reader'));
insert into csm_user_group (user_group_id,user_id,group_id) values (csm_user_grou_user_group_i_seq.nextval,(select user_id from csm_user where login_name = 'cctsdemo1@nci.nih.gov'),(select group_id from csm_group where group_name = 'data_analyst'))


