package com.xiaomi.mobilestats.common;

import java.io.File;
import java.util.Comparator;

public class FileNameComparator  implements Comparator<File>{

		@Override
		public int compare(File lhs, File rhs) {
			String fileName1 = lhs.getName();
			fileName1 = fileName1.substring(0, fileName1.indexOf("_"));
			String fileName2 = rhs.getName();
			fileName2 = fileName2.substring(0, fileName2.indexOf("_"));
			try {
				Long time1 = Long.parseLong(fileName1);
				Long time2 = Long.parseLong(fileName2);
				if(time1>time2){
					return 1;
				}else{
					return -1;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			return 0;
		}
		
	}