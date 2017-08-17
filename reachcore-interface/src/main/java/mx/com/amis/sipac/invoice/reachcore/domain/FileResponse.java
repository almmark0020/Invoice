package mx.com.amis.sipac.invoice.reachcore.domain;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Representation of a response result of a file request to reachcore
 *
 */
public class FileResponse implements Serializable {
  private static final long serialVersionUID = 5173206396595360095L;
  
  /**
   * Indicates wether or not the request was succesful (status 200)
   */
  private boolean success;
  
  /**
   * In case of error, contains the error message provided by reachcore
   */
  private String errorMsg;
  
  /**
   * Binary representation of the requested file in case the request was successful
   */
  private byte[] contents;
  
  public boolean isSuccess() {
    return success;
  }
  public void setSuccess(boolean success) {
    this.success = success;
  }
  public String getErrorMsg() {
    return errorMsg;
  }
  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }
  public byte[] getContents() {
    return contents;
  }
  public void setContents(byte[] contents) {
    this.contents = contents;
  }
  
  @Override
  public String toString() {
    return "FileResponse [success=" + success + ", errorMsg=" + errorMsg + ", contents=" + Arrays.toString(contents)
        + "]";
  }
}
