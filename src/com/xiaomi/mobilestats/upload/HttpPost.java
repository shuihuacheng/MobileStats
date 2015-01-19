package com.xiaomi.mobilestats.upload;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.apache.http.HttpStatus;

import com.xiaomi.mobilestats.controller.LogController;

public class HttpPost {
    /**
     * 通过拼接的方式构造请求内容，实现参数传输以及文件传输
     * @param actionUrl
     * @param params
     * @param files
     * @return
     * @throws IOException
     */
    public static String post( String actionUrl , Map < String , String > params , Map < String , FormFile > files ) throws IOException
      {
        String BOUNDARY = java.util.UUID.randomUUID ( ).toString ( ) ;
        String PREFIX = "--" , LINEND = "\r\n" ;
        String MULTIPART_FROM_DATA = "multipart/form-data" ;
        String CHARSET = "UTF-8" ;

        URL uri = new URL ( actionUrl ) ;
        HttpURLConnection conn = ( HttpURLConnection ) uri .openConnection ( ) ;
        conn.setReadTimeout ( 5 * 1000 ) ; // 缓存的最长时间
        conn.setDoInput ( true ) ;// 允许输入
        conn.setDoOutput ( true ) ;// 允许输出
        conn.setUseCaches ( false ) ; // 不允许使用缓存
        conn.setRequestMethod ( "POST" ) ;
        conn.setRequestProperty ( "connection" , "keep-alive" ) ;
        conn.setRequestProperty ( "Charsert" , "UTF-8" ) ;
        conn.setRequestProperty ( "Content-Type" , MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY ) ;
        
        // 首先组拼文本类型的参数
        StringBuilder sb = new StringBuilder ( ) ;
        for ( Map.Entry < String , String > entry : params.entrySet ( ) )
          {
            sb.append ( PREFIX ) ;
            sb.append ( BOUNDARY ) ;
            sb.append ( LINEND ) ;
            sb.append ( "Content-Disposition: form-data; name=\"" + entry.getKey ( ) + "\"" + LINEND ) ;
            sb.append ( "Content-Type: text/plain; charset=" + CHARSET + LINEND ) ;
            sb.append ( "Content-Transfer-Encoding: 8bit" + LINEND ) ;
            sb.append ( LINEND ) ;
            sb.append ( entry.getValue ( ) ) ;
            sb.append ( LINEND ) ;
          }
        
        DataOutputStream outStream = new DataOutputStream ( conn.getOutputStream ( ) ) ;
        outStream.write ( sb.toString ( ).getBytes ( ) ) ;
        // 发送文件数据
        if ( files != null )
          for ( Map.Entry < String , FormFile > file : files.entrySet ( ) )
            {
              StringBuilder sb1 = new StringBuilder ( ) ;
              sb1.append ( PREFIX ) ;
              sb1.append ( BOUNDARY ) ;
              sb1.append ( LINEND ) ;
              sb1.append ( "Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getKey ( ) + "\"" + LINEND ) ;
              sb1.append ( "Content-Type: application/octet-stream; charset=" + CHARSET + LINEND ) ;
              sb1.append ( LINEND ) ;
              outStream.write ( sb1.toString ( ).getBytes ( ) ) ;

              RandomAccessFile randomFileInputStream = new RandomAccessFile(file.getValue().getFile(), "r");
              randomFileInputStream.seek(file.getValue().getUpload_end());
//              InputStream is = new FileInputStream (file.getValue().getFile() ) ;
              byte [ ] buffer = new byte [ 1024 ] ;
              int len = 0 ;
              while ( ( len = randomFileInputStream.read ( buffer ) ) != - 1 )
                {
                  outStream.write ( buffer , 0 , len ) ;
                }
              randomFileInputStream.close ( ) ;
              outStream.write ( LINEND.getBytes ( ) ) ;
            }

        // 请求结束标志
        byte [ ] end_data = ( PREFIX + BOUNDARY + PREFIX + LINEND ) .getBytes ( ) ;
        outStream.write ( end_data ) ;
        outStream.flush ( ) ;
        // 得到响应码
        int res = conn.getResponseCode ( ) ;
        InputStream in = conn.getInputStream ( ) ;
        if ( res == HttpStatus.SC_OK )
          {
            int ch ;
            StringBuilder sb2 = new StringBuilder ( ) ;
            while ( ( ch = in.read ( ) ) != - 1 )
              {
                sb2.append ( ( char ) ch ) ;
              }
          }
        outStream.close ( ) ;
        conn.disconnect ( ) ;
        return in.toString ( ) ;
      }
    
    /**
     * 上传文件,如果已经上传过则从服务端获取已上传位置,否则全文件上传
     * @param actionUrl
     * @param params
     * @param uploadFile
     * @return
     * @throws IOException
     */
    public String uploadFile(String actionUrl,Map < String , String > params,FormFile uploadFile) throws IOException{
    	   long position  = -1;
	    	if(uploadFile != null && uploadFile.isHasUploaded()){
	    		position = getUploadedPosition(uploadFile);
	    	}else{
	    		addUploadFileRecord(uploadFile);
	    	}
    	
    		String BOUNDARY = java.util.UUID.randomUUID ( ).toString ( ) ;
    		String PREFIX = "--" , LINEND = "\r\n" ;
    		String MULTIPART_FROM_DATA = "multipart/form-data" ;
    		String CHARSET = "UTF-8" ;
    		
    		URL uri = new URL ( actionUrl ) ;
    		HttpURLConnection conn = ( HttpURLConnection ) uri .openConnection ( ) ;
    		conn.setReadTimeout ( 5 * 1000 ) ; // 缓存的最长时间
    		conn.setDoInput ( true ) ;// 允许输入
    		conn.setDoOutput ( true ) ;// 允许输出
    		conn.setUseCaches ( false ) ; // 不允许使用缓存
    		conn.setRequestMethod ( "POST" ) ;
    		conn.setRequestProperty ( "connection" , "keep-alive" ) ;
    		conn.setRequestProperty ( "Charsert" , "UTF-8" ) ;
    		conn.setRequestProperty ( "Content-Type" , MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY ) ;
    		
    		// 首先组拼文本类型的参数
    		StringBuilder sb = new StringBuilder ( ) ;
    		for ( Map.Entry < String , String > entry : params.entrySet ( ) )
    		{
    			sb.append ( PREFIX ) ;
    			sb.append ( BOUNDARY ) ;
    			sb.append ( LINEND ) ;
    			sb.append ( "Content-Disposition: form-data; name=\"" + entry.getKey ( ) + "\"" + LINEND ) ;
    			sb.append ( "Content-Type: text/plain; charset=" + CHARSET + LINEND ) ;
    			sb.append ( "Content-Transfer-Encoding: 8bit" + LINEND ) ;
    			sb.append ( LINEND ) ;
    			sb.append ( entry.getValue ( ) ) ;
    			sb.append ( LINEND ) ;
    		}
    		DataOutputStream outStream = new DataOutputStream ( conn.getOutputStream ( ) ) ;
    		outStream.write ( sb.toString ( ).getBytes ( ) ) ;
    		
    		if(uploadFile != null){
                StringBuilder sb1 = new StringBuilder ( ) ;
                sb1.append ( PREFIX ) ;
                sb1.append ( BOUNDARY ) ;
                sb1.append ( LINEND ) ;
                sb1.append ( "Content-Disposition: form-data; name=\"file\"; filename=\"" + uploadFile.getFile().getName() + "\"" + LINEND ) ;
                sb1.append ( "Content-Type: application/octet-stream; charset=" + CHARSET + LINEND ) ;
                sb1.append ( LINEND ) ;
                outStream.write ( sb1.toString ( ).getBytes ( ) ) ;
                
               if(uploadFile != null && uploadFile.isHasUploaded() && position > 0){
            	   RandomAccessFile randomInputStream  = new RandomAccessFile(uploadFile.getFile(),"rwd");
            	   randomInputStream.seek(position);
                   byte [ ] buffer = new byte [1024 ] ;
                   int len = 0 ;
                   while ( ( len = randomInputStream.read ( buffer ) ) != - 1 )
                     {
                       outStream.write ( buffer , 0 , len ) ;
                     }
                   randomInputStream.close ( ) ;
                   outStream.write ( LINEND.getBytes ( ) ) ;
               }else{
            	   InputStream is = new FileInputStream (uploadFile.getFile()) ;
            	   byte [ ] buffer = new byte [ 1024 ] ;
            	   int len = 0 ;
            	   while ( ( len = is.read ( buffer ) ) != - 1 )
            	   {
            		   outStream.write ( buffer , 0 , len ) ;
            	   }
            	   is.close ( ) ;
            	   outStream.write ( LINEND.getBytes ( ) ) ;
               }
    		}
    		
            // 请求结束标志
            byte [ ] end_data = ( PREFIX + BOUNDARY + PREFIX + LINEND ) .getBytes ( ) ;
            outStream.write ( end_data ) ;
            outStream.flush ( ) ;
            // 得到响应码
            int res = conn.getResponseCode ( ) ;
            InputStream in = conn.getInputStream ( ) ;
            if ( res == HttpStatus.SC_OK )
              {
                int ch ;
                StringBuilder sb2 = new StringBuilder ( ) ;
                while ( ( ch = in.read ( ) ) != - 1 )
                  {
                    sb2.append ( ( char ) ch ) ;
                  }
              }
            outStream.close ( ) ;
            conn.disconnect ( ) ;
            return in.toString ( ) ;

    }


	/**
     * 获取文件已经上传位置
     * @param uploadFile
     * @return
     */
	private long getUploadedPosition(FormFile uploadFile) {
		// TODO Auto-generated method stub
		return 0;
	}
    
	/**
	 * 添加日志文件上传记录
	 * @param uploadFile
	 */
	private void addUploadFileRecord(FormFile uploadFile) {
		File uploadFileRecord = new File(LogController.baseFilePath+"MobileStats"+File.separator+"uploadRecord.txt");
		if(!uploadFileRecord.getParentFile().exists()){
			uploadFileRecord.getParentFile().mkdirs();
		}
		if(!uploadFileRecord.exists()){
			try {
				uploadFileRecord.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
