package riz.silvano.intervaltree.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import riz.silvano.intervaltree.Interval;
import riz.silvano.intervaltree.IntervalFile;

public class IntervalFileLoader {

	public static List<Interval> loadIntervalsFromFiles(IntervalFile[] intervalFiles) {
		List<Interval> ret = new ArrayList<Interval>();
		BufferedReader br = null;
		for (int fileIdx = 0; fileIdx < intervalFiles.length; fileIdx++) {
			try {
				IntervalFile file = intervalFiles[fileIdx];
				if (new File(file.getFileName()).exists()) {
					br = new BufferedReader(new FileReader(file.getFileName()));					
				} else {
					InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(file.getFileName());
					if (is != null) {
						br = new BufferedReader(new InputStreamReader(is));
					} else {
						throw new RuntimeException("File " + file.getFileName() + " does not exist as local file or classpath resource");
					}
				}
				String line;
				while ((line = br.readLine()) != null) {
					Interval interval = parseLine(line, file.getSupplier(), fileIdx);
					if (interval != null) {
						ret.add(interval);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					// Do nothing.
				}
			}
		}
		return ret;
	}

	private static Interval parseLine(String line, String supplier, int fileId) {
		if ("AIB".equals(supplier)) {
			long min = Long.parseLong(line.substring(12, 24));
			long max = Long.parseLong(line.substring(0, 12));
			String processor = line.substring(24, 25);
			String issuer = line.substring(36, 116).trim();

			return new Interval(min, max, processor + ", " + issuer, fileId);
		} else if ("ELAVON".equals(supplier)) {
			if (line.startsWith("000ENH010")) {
				// It's just the header line
				return null;
			}
			String[] parts = line.split(",");
			long min = Long.parseLong(parts[0]);
			long max = Long.parseLong(parts[1]);
			String info = parts[5] + ", " + parts[6];
			return new Interval(min, max, info, fileId);

		} else {
			throw new RuntimeException("Unsupported BIN File Supplier [" + supplier + "]");
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IntervalFile[] files = new IntervalFile[2];
		IntervalFile file1 = new IntervalFile("BIN021813.dat", "AIB", "2013-02-18");
		IntervalFile file2 = new IntervalFile("ELAVON BIN.ACCTRNG_19022013", "ELAVON", "2013-02-19");
		files[0] = file1;
		files[1] = file2;

		List<Interval> intervals = IntervalFileLoader.loadIntervalsFromFiles(files);
		System.out.println("Loaded from files. List length is " + intervals.size());

	}

}
