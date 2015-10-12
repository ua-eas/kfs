# sissom@jsissom-mbp rice-data (master) $ cd bootstrap/
# jsissom@jsissom-mbp bootstrap (master) $ ls
# krcr_cmpnt_t.csv    krim_entity_ent_typ_t.csv krim_prncpl_t.csv   krim_role_t.csv     krlc_pstl_cd_t.csv
# krcr_nmspc_t.csv    krim_entity_nm_t.csv    krim_role_mbr_attr_data_t.csv krim_rsp_attr_data_t.csv  krlc_st_t.csv
# krcr_parm_t.csv     krim_entity_t.csv   krim_role_mbr_t.csv   krim_rsp_t.csv
# krim_attr_defn_t.csv    krim_perm_attr_data_t.csv krim_role_perm_t.csv    krim_typ_attr_t.csv
# krim_dlgn_mbr_t.csv   krim_perm_t.csv     krim_role_rsp_actn_t.csv  krim_typ_t.csv
# krim_dlgn_t.csv     krim_perm_tmpl_t.csv    krim_role_rsp_t.csv   krlc_cnty_t.csv
# jsissom@jsissom-mbp bootstrap (master) $ ls ../demo/
# krcr_parm_t.csv     krim_entity_email_t.csv   krim_entity_phone_t.csv   krim_grp_t.csv      krlc_cmp_t.csv
# krim_dlgn_mbr_attr_data_t.csv krim_entity_emp_info_t.csv  krim_entity_priv_pref_t.csv krim_prncpl_t.csv
# krim_dlgn_mbr_t.csv   krim_entity_ent_typ_t.csv krim_entity_t.csv   krim_role_mbr_attr_data_t.csv
# krim_entity_addr_t.csv    krim_entity_ext_id_t.csv  krim_grp_attr_data_t.csv  krim_role_mbr_t.csv
# krim_entity_afltn_t.csv   krim_entity_nm_t.csv    krim_grp_mbr_t.csv    krim_role_rsp_actn_t.csv
# jsissom@jsissom-mbp bootstrap (master) $

Dir["/Users/jsissom/git/financials/kfs-core/src/main/resources/org/kuali/rice/db/phase2/*.xml"].each do |f|
  puts f
end
