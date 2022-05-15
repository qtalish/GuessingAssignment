package guess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Liar<T> implements Guesser<Liar.Secret<T>> {

	public static class Secret<T> {
		private String secret;
		private int lies;

		public Secret(String secret, int lies) {
			super();
			this.secret = secret;
			this.lies = lies;
		}

		@Override
		public String toString() {
			return "The secret is: " + secret + " (with " + lies + " lie)";
		}

		public int getLies() {
			return 0;
		}

		public Integer getSecret() {
			return null;
		}

	}
	public int maxLies;
	private Set<String> colors;
	private double progress;
	private double yes;
	private double no;
	private int lie;
	private String color;
	private boolean firstTime = true;
	Map<String, Set<String>> map = new HashMap<>();
	Set<String> list1;
	Set<String> list2;
	int loop = 0;
	int k = 0;

	public Liar(Set<String> colors, int maxLies, String color) {
		// TODO Auto-generated constructor stub
		this.maxLies = maxLies;
		this.colors = colors;
		this.color = color;
	}

	public int setColors(int n) {
		return n;
	}

	@Override
	public String initialize() {
		// TODO Auto-generated method stub
		String col = "Pick one among";
		List<String> l = new ArrayList<String>();
		for (String c : colors) {
			col = col + " " + c;
			l.add(c);
		}
		map.put("0", colors);
		return col;
	}

	@Override
	public boolean hasSolved() {
		if (progress == 1.0) {
			progress = 0;
			yes = 0;
			no = 0;
			return true;
		}
		return false;
	}

	@Override
	public Liar.Secret<T> getSecret() {
		if (map.containsKey("dump")) {
			Set<String> dump = map.get("dump");
			for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
				Set<String> s = new HashSet<>();
				for (String d : entry.getValue()) {
					if (!dump.contains(d))
						s.add(d);
				}
				map.put(entry.getKey(), s);
			}
		}
		for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
			if (entry.getKey().equals("dump"))
				continue;
			Set<String> s = new HashSet<>();
			for (String d : entry.getValue()) {
				color = d;
				lie = Integer.valueOf(entry.getKey());
			}
			map.put(entry.getKey(), s);
		}
		return new Secret<T>(color, lie);
	}

	@Override
	public void yes() {
		yes++;

		int count = 0;
		Set<String> set2 = new HashSet<>();
		for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
			if (!entry.getKey().equals("dump")) {
				Set<String> set = new HashSet<>();
				if (count != 0) {
					set.addAll(set2);
					set2 = new HashSet<String>();
				}
				for (String s : entry.getValue()) {
					if (!list2.contains(s)) {
						set.add(s);
					} else {
						if (!entry.getKey().equals(String.valueOf(lie))) {
							set2.add(s);
							list2.remove(s);
						}
					}
				}
				entry.setValue(set);
				count++;
			}
		}
		if (lie == maxLies) {
			if (map.containsKey("dump")) {
				Set<String> s = map.get("dump");
				for (String l : list2)
					s.add(l);
				map.put("dump", s);
			} else {
				yes--;
				map.put("dump", list2);
			}
		}
		if (lie < maxLies) {
			lie++;
			map.put(String.valueOf(lie), new HashSet<>(list2));
		}
	}

	@Override
	public void no() {
		no++;
		int count = 0;
		Set<String> set2 = new HashSet<>();
		for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
			if (!entry.getKey().equals("dump")) {
				Set<String> set = new HashSet<>();
				if (count != 0) {
					set.addAll(set2);
					set2 = new HashSet<String>();
				}
				for (String s : entry.getValue()) {
					if (!list1.contains(s)) {
						set.add(s);
					} else {
						if (!entry.getKey().equals(String.valueOf(lie))) {
							set2.add(s);
							list1.remove(s);
						}
					}
				}
				entry.setValue(set);
				count++;
			}
		}
		if (lie == maxLies) {
			if (map.containsKey("dump")) {
				Set<String> s = map.get("dump");
				for (String l : list1)
					s.add(l);
				map.put("dump", s);
			} else {
				no--;
				map.put("dump", list1);
			}
		}
		if (lie < maxLies) {
			lie++;
			map.put(String.valueOf(lie), new HashSet<>(list1));
		}
	}

	@Override
	public String makeQuestion() {
		// TODO Auto-generated method stub
		String col = "Is the secret string among";
		list1 = new HashSet<>();
		list2 = new HashSet<>();
		int k = 0;
		int si = 0;
		for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
			int cu = 0;
			if (!entry.getKey().equals("dump")) {
				List<String> la = new ArrayList<String>(entry.getValue());
				int l = 0;
				int size = entry.getValue().size();
				cu = size;
				size = size / 2 + size % 2;
				for (String s : entry.getValue()) {
					if (l < size && k != colors.size() / 2) {
						if (si == 1 && cu == 1) {
							list2.add(s);
						} else {
							col = col + " " + s;
							list1.add(s);
						}
						l++;
						k++;
					} else {
						list2.add(s);
					}
				}
				if(size!=0)
					si = size;
			}
		}
		loop++;
		firstTime = false;
		return col + " ?";
	}

	@Override
	public double progress() {
		double p = progress;
		progress = (double) yes + no;
		int dumpSize = 0;
		for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
			if (entry.getKey().equals("dump")) {
				dumpSize = entry.getValue().size();
			}
		}
		double size = colors.size();
		progress = progress / size;
		if (dumpSize == colors.size() - 1)
			progress = 1;
		if (progress > 1)
			progress = 1;
		if(progress ==1 && dumpSize!=colors.size()-1)
			progress = p;
		return progress;
	}

}
