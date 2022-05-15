package guess;

public class HiLo implements Guesser<Integer> {

	private int min;
	private int max;
	private double progress;
	private int half;
	private boolean firstTime = true;
	private int secret;
	final int minimum;
	final int maximum;
	
	public HiLo(int min, int max) {
		super();
		this.min = min;
		this.max = max;
		minimum = min;
		maximum = max;
	}

	@Override
	public String initialize() {
		// TODO Auto-generated method stub
		return "Pick a number between "+minimum+" to "+maximum;
	}

	@Override
	public boolean hasSolved() {
		// TODO Auto-generated method stub
		if(min == max) {
			progress = 1;
			secret = max;
			min = minimum;
			max = maximum;
		}
		if (progress == 1.0) {
			progress = 0;
			firstTime =true;
			return true;
		}
		return false;
	}

	@Override
	public Integer getSecret() {
		// TODO Auto-generated method stub
		return secret;
	}

	@Override
	public void yes() {
		// TODO Auto-generated method stub
		min = half+1;
		half = max/2;
		half = half + half/2+1;
		if(half<min) {
			half = min;
		}
		if(min == max) {
			progress = 1;
			secret = max;
		}
	}

	@Override
	public void no() {
		// TODO Auto-generated method stub
		max = half;
		half = max/2 + max%2;
		if(half<min) {
			half = max - 1;
		}
	}

	@Override
	public String makeQuestion() {
		// TODO Auto-generated method stub
		if(firstTime) {
			half = max/2;
			firstTime = false;
		}
		return "Is your number larger than "+half +"?";
	}

	@Override
	public double progress() {
		// TODO Auto-generated method stub
		return (double)min/max;
	}

}
