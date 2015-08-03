#!/bin/bash
EXTENSION='.ctl'
declare -a TABLENAMES=('intervention_sites' 'studies' 'treatment_assignment' 'epochs' 'participant_assignments' 'ae_reporting_periods' 'ae_reports' 'ae_routine_reports' 'adverse_events' 'caaers_users' 'research_staffs' 'investigators' 'report_versions' 'ae_report_people' 'additional_information' 'additional_info_document' 'adverseevent_index' 'ae_cause_types' 'ae_attributions' 'ae_behavioral_interventions' 'ae_biological_interventions' 'ae_dietary_interventions' 'ae_genetic_interventions' 'ae_labs' 'devices' 'study_devices' 'ae_medical_devices' 'ae_other_interventions' 'pre_existing_conditions' 'ae_pre_existing_conds' 'prior_therapies' 'ae_prior_therapies' 'other_interventions' 'ae_radiation_interventions' 'config_properties' 'report_calendar_templates' 'ae_recom_reports' 'ae_report_descriptions' 'ae_surgery_interventions' 'ae_terms' 'agent_terms' 'agents' 'anatomic_sites' 'arms' 'planned_notifications' 'audit_event_values' 'audit_events' 'caaers_bootstrap_log' 'caaers_field_defs' 'chemo_agents' 'concomitant_medications' 'conditions' 'configuration' 'contact_mechanisms' 'treatments' 'course_agents' 'csm_application' 'csm_filter_clause' 'csm_group' 'csm_mapping' 'csm_protection_element' 'csm_protection_group' 'csm_pg_pe' 'csm_privilege' 'csm_role' 'csm_role_privilege' 'csm_user' 'csm_user_group' 'csm_user_group_role_pg' 'csm_user_pe' 'ctc_versions' 'ctc_categories' 'ctc_terms' 'ctc_grades' 'disease_categories' 'study_diseases' 'disease_histories' 'disease_terminologies' 'disease_terms' 'expected_aes' 'expedited_ae_index' 'ext_ae_reporting_prds' 'ext_adverse_events' 'handles' 'participants' 'organizations' 'identifiers' 'investigational_new_drugs' 'ind_holders' 'integration_logs' 'integration_log_details' 'integration_log_message' 'investigator_index' 'lab_versions' 'lab_categories' 'lab_terms' 'labs' 'mandatory_field_defs' 'meddra_versions' 'meddra_hlgt' 'meddra_hlt' 'meddra_hlgt_hlt' 'meddra_pt' 'meddra_hlt_pt' 'meddra_llt' 'meddra_soc' 'meddra_soc_hlgt' 'metastatic_disease_sites' 'nas' 'notifications' 'observed_ae_profiles' 'organization_index' 'other_causes' 'outcomes' 'participant_histories' 'participant_index' 'password_history' 'password_policy' 'prior_therapy_agents' 'recipients' 'reconciliation_reports' 'reconciled_adverse_events' 'report_delivery_defs' 'report_schedules' 'report_deliveries' 'report_format' 'report_index' 'report_tracking_status' 'report_tracking' 'reported_adverse_events' 'reportingperiod_index' 'researchstaff_index' 'role_privilege' 'rule_sets' 'scheduled_notifications' 'searches' 'site_investigators' 'site_research_staffs' 'site_rs_staff_roles' 'solicited_events' 'spa_concomitant_medications' 'spa_disease_histories' 'spa_metastatic_disease_sites' 'spa_pre_existing_conds' 'spa_prior_therapies' 'spa_prior_therapy_agents' 'study_agents' 'study_agent_inds' 'study_amendments' 'study_device_inds' 'study_index' 'study_interventions_exp_aes' 'study_organizations' 'study_investigators' 'study_personnel' 'workflow_configuration' 'study_site_wf_cfgs' 'study_therapy' 'ta_study_interventions' 'ta_agents' 'ta_devices' 'ta_expected_ae_intervention' 'ta_other_interventions' 'task_configuration' 'terminologies' 'wf_assignees' 'wf_review_comments' 'wf_transition_configs' 'wf_transition_owners')
for(( i=0;i<${#TABLENAMES[@]};i++))	do	sqlldr USERNAME/PASSWORD@//HOSTNAME:1521/ORCL control=${TABLENAMES[i]}${EXTENSION}
done