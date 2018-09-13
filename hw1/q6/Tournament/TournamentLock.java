package q6.Tournament;

import java.util.Arrays;

public class TournamentLock implements Lock {
	
	volatile int numThreads;
	volatile int[] last;
	volatile int[] level;
	
    public TournamentLock(int numThreads){
        this.numThreads = numThreads;
        this.last = new int[numThreads];
        this.level = new int[numThreads];
    }

	@Override
	public void lock(int pid) {
		for(int lvl = 1; lvl < numThreads; ++lvl) {
			level[pid] = lvl;
			last[lvl] = pid;
			for(int proc = 0; proc < numThreads; ++proc) {
				while(true) {
					if((proc == pid) || (last[lvl] != pid) || (level[proc] < lvl)) {
						break;
					}
				}
			}
		}
		
	}

	@Override
	public void unlock(int pid) {
		level[pid] = 0;
	}
}
