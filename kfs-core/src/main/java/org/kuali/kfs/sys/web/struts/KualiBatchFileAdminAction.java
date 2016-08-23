/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.web.struts;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.kns.question.ConfirmationQuestion;
import org.kuali.kfs.kns.web.struts.action.KualiAction;
import org.kuali.kfs.krad.exception.AuthorizationException;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.BatchFile;
import org.kuali.kfs.sys.batch.BatchFileUtils;
import org.kuali.kfs.sys.batch.service.BatchFileAdminAuthorizationService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FileStorageService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.RiceConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.text.MessageFormat;

public class KualiBatchFileAdminAction extends KualiAction {
    public ActionForward download(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiBatchFileAdminForm fileAdminForm = (KualiBatchFileAdminForm) form;
        String filePath = BatchFileUtils.resolvePathToAbsolutePath(fileAdminForm.getFilePath());
        FileStorageService fileStorageService = SpringContext.getBean(FileStorageService.class);
        String directoryName = StringUtils.substringBeforeLast(filePath, fileStorageService.separator());
        String fileName = StringUtils.substringAfterLast(filePath, fileStorageService.separator());

        if (!fileStorageService.fileExists(filePath)) {
            throw new RuntimeException("Error: non-existent file or directory provided");
        }
        if (!BatchFileUtils.isDirectoryAccessible(directoryName)) {
            throw new RuntimeException("Error: inaccessible directory provided");
        }

        // TODO: Eliminate use of File class altogether, once BatchFile class is refactored.
        File file = new File(filePath).getAbsoluteFile();
        BatchFile batchFile = new BatchFile();
        batchFile.setFile(file);
        if (!SpringContext.getBean(BatchFileAdminAuthorizationService.class).canDownload(batchFile, GlobalVariables.getUserSession().getPerson())) {
            throw new RuntimeException("Error: not authorized to download file");
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setContentLength((int) fileStorageService.getFileLength(filePath));

        InputStream fis = fileStorageService.getFileStream(filePath);
        IOUtils.copy(fis, response.getOutputStream());
        response.getOutputStream().flush();
        return null;
    }

    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiBatchFileAdminForm fileAdminForm = (KualiBatchFileAdminForm) form;
        String filePath = BatchFileUtils.resolvePathToAbsolutePath(fileAdminForm.getFilePath());
        FileStorageService fileStorageService = SpringContext.getBean(FileStorageService.class);
        String directoryName = StringUtils.substringBeforeLast(filePath, fileStorageService.separator());

        ConfigurationService kualiConfigurationService = SpringContext.getBean(ConfigurationService.class);

        if (!fileStorageService.fileExists(filePath)) {
            throw new RuntimeException("Error: non-existent file or directory provided");
        }
        if (!BatchFileUtils.isDirectoryAccessible(directoryName)) {
            throw new RuntimeException("Error: inaccessible directory provided");
        }

        // TODO: Eliminate use of File class altogether, once BatchFile class is refactored.
        File file = new File(filePath).getAbsoluteFile();
        BatchFile batchFile = new BatchFile();
        batchFile.setFile(file);
        if (!SpringContext.getBean(BatchFileAdminAuthorizationService.class).canDelete(batchFile, GlobalVariables.getUserSession().getPerson())) {
            throw new RuntimeException("Error: not authorized to delete file");
        }

        String displayFileName = BatchFileUtils.pathRelativeToRootDirectory(filePath);

        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        if (question == null) {
            String questionText = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.QUESTION_BATCH_FILE_ADMIN_DELETE_CONFIRM);
            questionText = MessageFormat.format(questionText, displayFileName);
            return performQuestionWithoutInput(mapping, fileAdminForm, request, response, "confirmDelete", questionText,
                KRADConstants.CONFIRMATION_QUESTION, "delete", fileAdminForm.getFilePath());
        } else {
            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if ("confirmDelete".equals(question)) {
                String status = null;
                if (ConfirmationQuestion.YES.equals(buttonClicked)) {
                    try {
                        fileStorageService.delete(filePath);
                        status = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.MESSAGE_BATCH_FILE_ADMIN_DELETE_SUCCESSFUL);
                        status = MessageFormat.format(status, displayFileName);
                    } catch (Exception e) {
                        status = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.MESSAGE_BATCH_FILE_ADMIN_DELETE_ERROR);
                        status = MessageFormat.format(status, displayFileName);
                    }
                } else if (ConfirmationQuestion.NO.equals(buttonClicked)) {
                    status = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.MESSAGE_BATCH_FILE_ADMIN_DELETE_CANCELLED);
                    status = MessageFormat.format(status, displayFileName);
                }
                if (status != null) {
                    request.setAttribute("status", status);
                    return mapping.findForward(RiceConstants.MAPPING_BASIC);
                }
            }
            throw new RuntimeException("Unrecognized question: " + question + " or response: " + buttonClicked);
        }
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#checkAuthorization(org.apache.struts.action.ActionForm, java.lang.String)
     */
    @Override
    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {
        // do nothing... authorization is integrated into action handler
    }
}
