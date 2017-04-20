package edu.arizona.kfs.module.purap;

import edu.arizona.kfs.module.purap.PurapConstants;

public class PurapWorkflowConstants extends org.kuali.kfs.module.purap.PurapWorkflowConstants {
    
    public interface NodeDetails {
        public String getName();

        public String getAwaitingStatusCode();

        public String getDisapprovedStatusCode();

        public NodeDetails getPreviousNodeDetails();

        public NodeDetails getNextNodeDetails();

        public NodeDetails getNodeDetailByName(String name);

        public int getOrdinal();
    }
    
    
    public static class RequisitionDocument {
        public enum NodeDetailEnum implements NodeDetails {
        ADHOC_REVIEW(DOC_ADHOC_NODE_NAME, null, PurapConstants.RequisitionStatuses.APPDOC_CANCELLED), 
        CONTENT_REVIEW(PurapConstants.RequisitionStatuses.NODE_CONTENT_REVIEW, PurapConstants.RequisitionStatuses.APPDOC_AWAIT_CONTENT_REVIEW, PurapConstants.RequisitionStatuses.APPDOC_DAPRVD_CONTENT), 
        HAS_ACCOUNTING_LINES(PurapConstants.RequisitionStatuses.NODE_HAS_ACCOUNTING_LINES, PurapConstants.RequisitionStatuses.APPDOC_AWAIT_HAS_ACCOUNTING_LINES, PurapConstants.RequisitionStatuses.APPDOC_DAPRVD_HAS_ACCOUNTING_LINES), 
        SUB_ACCOUNT_REVIEW(PurapConstants.RequisitionStatuses.NODE_SUBACCOUNT, PurapConstants.RequisitionStatuses.APPDOC_AWAIT_SUB_ACCT_REVIEW, PurapConstants.RequisitionStatuses.APPDOC_DAPRVD_SUB_ACCT), 
        ACCOUNT_REVIEW(PurapConstants.RequisitionStatuses.NODE_ACCOUNT, PurapConstants.RequisitionStatuses.APPDOC_AWAIT_FISCAL_REVIEW, PurapConstants.RequisitionStatuses.APPDOC_DAPRVD_FISCAL), 
        ORG_REVIEW(PurapConstants.RequisitionStatuses.NODE_ORG_REVIEW, PurapConstants.RequisitionStatuses.APPDOC_AWAIT_CHART_REVIEW, PurapConstants.RequisitionStatuses.APPDOC_DAPRVD_CHART), 
        COMMODITY_CODE_REVIEW(PurapConstants.RequisitionStatuses.NODE_COMMODITY_CODE_REVIEW, PurapConstants.RequisitionStatuses.APPDOC_AWAIT_COMMODITY_CODE_REVIEW, PurapConstants.RequisitionStatuses.APPDOC_DAPRVD_COMMODITY_CODE), 
        SEPARATION_OF_DUTIES_REVIEW(PurapConstants.RequisitionStatuses.NODE_SEPARATION_OF_DUTIES, PurapConstants.RequisitionStatuses.APPDOC_AWAIT_SEP_OF_DUTY_REVIEW, PurapConstants.RequisitionStatuses.APPDOC_DAPRVD_SEP_OF_DUTY),
        // REQS & POA Should Not Be Editable on Additional Route Nodes Unless Specified
        OBJECT_SUB_TYPE_CODE_REVIEW(PurapConstants.RequisitionStatuses.NODE_OBJECT_SUB_TYPE_CODE, PurapConstants.RequisitionStatuses.APPDOC_AWAIT_OBJECT_SUB_TYPE_CODE_REVIEW, PurapConstants.RequisitionStatuses.APPDOC_DAPRVD_OBJECT_SUB_TYPE_CODE),
        // REQS needs to define statuses for sub fund approvals
        SUB_FUND_REVIEW(PurapConstants.RequisitionStatuses.NODE_SUB_FUND, PurapConstants.RequisitionStatuses.APPDOC_AWAIT_SUB_FUND_REVIEW, PurapConstants.RequisitionStatuses.APPDOC_DAPRVD_SUB_FUND);
        
            private final String name;
            private final String awaitingStatusCode;
            private final String disapprovedStatusCode;

            NodeDetailEnum(String name, String awaitingStatusCode, String disapprovedStatusCode) {
                this.name = name;
                this.awaitingStatusCode = awaitingStatusCode;
                this.disapprovedStatusCode = disapprovedStatusCode;
            }

            public String getName() {
                return name;
            }

            public String getAwaitingStatusCode() {
                return awaitingStatusCode;
            }

            public String getDisapprovedStatusCode() {
                return disapprovedStatusCode;
            }

            public NodeDetails getPreviousNodeDetails() {
                if (this.ordinal() > 0) {
                    return NodeDetailEnum.values()[this.ordinal() - 1];
                }
                return null;
            }

            public NodeDetails getNextNodeDetails() {
                if (this.ordinal() < (NodeDetailEnum.values().length - 1)) {
                    return NodeDetailEnum.values()[this.ordinal() + 1];
                }
                return null;
            }

            public NodeDetails getNodeDetailByName(String name) {
                return getNodeDetailEnumByName(name);
            }

            public static NodeDetails getNodeDetailEnumByName(String name) {
                for (NodeDetailEnum nodeDetailEnum : NodeDetailEnum.values()) {
                    if (nodeDetailEnum.name.equals(name)) {
                        return nodeDetailEnum;
                    }
                }
                return null;
            }

            // REQS & POA Should Not Be Editable on additional route nodes unless specified
            public static NodeDetails getNodeDetailEnumByStatus(String status) {
                for (NodeDetailEnum nodeDetailEnum : NodeDetailEnum.values()) {
                    if (nodeDetailEnum.awaitingStatusCode != null && nodeDetailEnum.awaitingStatusCode.equals(status)) {
                        return nodeDetailEnum;
                    }
                }
                return null;
            }
            
            public int getOrdinal() {
                return this.ordinal();
            }
        }
    }
}
