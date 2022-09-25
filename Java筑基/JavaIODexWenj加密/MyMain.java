

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FilenameFilter;


public class MyMain {

    public static void main(String[] args) throws Exception {
    	
    	byte[] mainDexData; //�洢Դapk�е�Դdex�ļ� 
    	byte[] aarData;     // �洢���еĿ�dex�ļ�
    	byte[] mergeDex;    // �洢��dex ��Դdex �ĺϲ�����dex�ļ�
    	
    	
    	File tempFileApk = new File("source/apk/temp");
    	if (tempFileApk.exists()) {
			File[]files = tempFileApk.listFiles();
			for(File file: files){
				if (file.isFile()) {
					file.delete();
				}
			}
		}
    	
    	File tempFileAar = new File("source/aar/temp");
    	if (tempFileAar.exists()) {
    		File[]files = tempFileAar.listFiles();
			for(File file: files){
				if (file.isFile()) {
					file.delete();
				}
			}
		}
    	
        /**
         * ��һ�� ����ԭʼapk ����dex
         *
         */
        AES.init(AES.DEFAULT_PWD);
        //��ѹapk
        File apkFile = new File("source/apk/app-debug.apk");
        File newApkFile = new File(apkFile.getParent() + File.separator + "temp");
        if(!newApkFile.exists()) {
        	newApkFile.mkdirs();
        }
        File mainDexFile = AES.encryptAPKFile(apkFile,newApkFile);
        if (newApkFile.isDirectory()) {
			File[] listFiles = newApkFile.listFiles();
			for (File file : listFiles) {
				if (file.isFile()) {
					if (file.getName().endsWith(".dex")) {
						// 将源dex文件进行重命名
						String name = file.getName();
						System.out.println("rename step1:"+name);
						int cursor = name.indexOf(".dex");
						String newName = file.getParent()+ File.separator + name.substring(0, cursor) + "_" + ".dex";
						System.out.println("rename step2:"+newName);
						file.renameTo(new File(newName));
					}
				}
			}
		}
        
        
    	 /**
         * aar libarye aar 除了没有签名文件 和apk一样 aar 里面是jar 不是dex 其实是一样的
		  * aar - unzip - jar - dex 壳
		  * 处理arr 过去壳dex
         */
    	File aarFile = new File("source/aar/mylibrary-debug.aar");
        File aarDex  = Dx.jar2Dex(aarFile);
//        aarData = Utils.getBytes(aarDex);   //��dex�ļ�����byte ����
        
        
        File tempMainDex = new File(newApkFile.getPath() + File.separator + "classes.dex");
        if (!tempMainDex.exists()) {
			tempMainDex.createNewFile();
		}
        //  壳子和 源dex 合并再一起 jar 转dex runtime.exec("Cmd.exe /c dx --dex --output)
//        System.out.println("MyMain" + tempMainDex.getAbsolutePath());
        FileOutputStream fos = new FileOutputStream(tempMainDex);
        byte[] fbytes = Utils.getBytes(aarDex);
        fos.write(fbytes);
        fos.flush();
        fos.close();


        /**
         * 打包签名文件
         */

        File unsignedApk = new File("result/apk-unsigned.apk");
        unsignedApk.getParentFile().mkdirs();
//        File disFile = new File(apkFile.getAbsolutePath() + File.separator+ "temp");
        Zip.zip(newApkFile, unsignedApk);
        // 不用插件就不能使用apk的源签名
		/*
		*         String cmd[] = {"cmd.exe", "/C ","jarsigner",  "-sigalg", "MD5withRSA",
                "-digestalg", "SHA1",
                "-keystore", "C:/Users/allen/.android/debug.keystore",
                "-storepass", "android",
                "-keypass", "android",
                "-signedjar", signedApk.getAbsolutePath(),
                unsignedApk.getAbsolutePath(),
                "androiddebugkey"};*/
        File signedApk = new File("result/apk-signed.apk");
        Signature.signature(unsignedApk, signedApk);
    }

    
	private static File getMainDexFile(File apkFile) {
		// TODO Auto-generated method stub
		File disFile = new File(apkFile.getAbsolutePath() + "unzip");
		Zip.unZip(apkFile, disFile);
		File[] files = disFile.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(".dex")) {
					return true;
				}
				return false;
			}
		});
		for (File file: files) {
			if (file.getName().endsWith("classes.dex")) {
				return file;
			}
		}
		return null;
	}
}
