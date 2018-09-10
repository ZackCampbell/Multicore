/* Peterson's solution to the mutual exclusion problem - 1981 */

bool turn, flag[2];
byte ncrit;

active [2] proctype user()
{
	assert(_pid == 0 || _pid == 1);
again:
    turn = _pid;
	flag[_pid] = 1;
	(flag[1 - _pid] == 0 || turn == 1 - _pid);

	ncrit++;
	assert(ncrit == 1);	/* critical section */
	ncrit--;

	flag[_pid] = 0;
	goto again
}
