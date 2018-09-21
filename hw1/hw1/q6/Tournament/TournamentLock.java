package q6.Tournament;

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
			setLockVars(pid, lvl);
			for(int proc = 0; proc < numThreads; ++proc) {
				while(true) {
					if((proc == pid) || (last[lvl] != pid) || (level[proc] < lvl)) {
						break;
					}
				}
			}
		}
		
	}
	
	private synchronized void setLockVars(int pid, int lvl) {
		level[pid] = lvl;
		last[lvl] = pid;
	}
	
	private synchronized void setUnlockVar(int pid) {
		level[pid] = 0;
	}

	@Override
	public void unlock(int pid) {
		setUnlockVar(pid);
	}
}