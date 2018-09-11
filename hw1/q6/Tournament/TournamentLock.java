package q6.Tournament;

import java.util.Arrays;

public class TournamentLock implements Lock {

//    volatile int N;
//    volatile int[] gate;
//    volatile int[] last;
//    
//    public TournamentLock(int numProc) {
//        N = numProc;
//        gate = new int[N];  // We only use gate[1]... gate[N-1]; gate[0] is unused
//        Arrays.fill(gate, 0);
//        last = new int[N];
//        Arrays.fill(last, 0);
//    }
//    public void lock(int i) {
//        for (int k = 1; k < N; k++) {
//            gate[i] = k;
//            last[k] = i;
//            for (int j = 0; j < N; j++) {
//                while ((j != i) &&          // There is some other process
//                        (gate[j] >= k) &&   // that is ahead or at the same level
//                        (last[k] == i)      // and I am the last to update last[k]
//                ) {}    // Busy Wait
//            }
//        }
//    }
//    public void unlock(int i) {
//        gate[i] = 0;
//    }
	
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
