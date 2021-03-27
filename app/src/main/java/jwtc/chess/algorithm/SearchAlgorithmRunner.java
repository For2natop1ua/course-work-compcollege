package jwtc.chess.algorithm;

import jwtc.chess.GameControl;
import jwtc.chess.JNI;
import jwtc.chess.Move;

public class SearchAlgorithmRunner implements Runnable{
	protected GameControl m_control;
	public SearchAlgorithmRunner(GameControl gc){
		m_control = gc;
	}
    // @Override
    public void run() {
    	try
		{
    		JNI _jni = m_control.getJNI();
    		if(_jni.isEnded() != 0)
    			return;
			
			m_control.disableControl();
			
			if(m_control.getLevelMode() == GameControl.LEVEL_PLY){
				
				int level = m_control.getLevelPly();
				_jni.searchDepth(level); 
				int move = _jni.getMove();
				m_control.sendMoveMessageFromThread(move);
				
				int evalCnt = _jni.getEvalCount();
				String s;
				
				if(evalCnt == 0){
					s = "From opening book";
				}
				else {
					s = "Searched at " + level + " ply";
				}
				m_control.sendMessageFromThread(s);
				
			} else {
				
				int level = m_control.getLevel();
				int secs[] = {1, 1, 2, 4, 8, 10, 20, 30, 60, 300, 900, 1800}; // 1 offset, so 3 extra 1 unused secs
				// start search thread
				_jni.searchMove(secs[level]);
				
				long lMillies = System.currentTimeMillis();
				int move, tmpMove, value, ply = 1, evalCnt, j, iSleep = 1000; String s;
				while(_jni.peekSearchDone() == 0){
					Thread.sleep(iSleep);
					ply = _jni.peekSearchDepth();
					s = "";
					if(ply > 5){
						ply = 5;
					}
					for(j = 0; j < ply; j++){
						tmpMove = _jni.peekSearchBestMove(j);
						if(tmpMove != 0)
							s += Move.toDbgString(tmpMove).replace("[", "").replace("]", "") + " ";
					}
					if(ply == 5){
						s += "...";
					}
					m_control.sendMessageFromThread(s);
				}
				move = _jni.getMove();
				m_control.sendMoveMessageFromThread(move);
				value = _jni.peekSearchBestValue();
				evalCnt = _jni.getEvalCount();
				
				if(evalCnt == 0){
					s = "";
				} else {
					int iTime = (int)((System.currentTimeMillis()-lMillies)/1000);
					s = iTime + " sec" + "\n\t";
				}
				m_control.sendMessageFromThread(s);
				
			} // else to level = 1
		}
		catch(Exception ex)
		{
			ex.printStackTrace(System.out);
		}
    }
} 
