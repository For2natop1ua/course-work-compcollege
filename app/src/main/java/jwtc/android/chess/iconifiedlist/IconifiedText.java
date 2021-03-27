package jwtc.android.chess.iconifiedlist;

public class IconifiedText implements Comparable<IconifiedText>{
    
	private String mText = "";

	public String getText() {
		return mText;
	}
	
	public void setText(String text) {
		mText = text;
	}

	public int compareTo(IconifiedText other) {
		if(this.mText != null)
			return this.mText.compareTo(other.getText()); 
		else 
			throw new IllegalArgumentException();
	}
}
