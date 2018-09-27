#!/bin/bash
start="$(date -u +%s.%N)"
./a file1.txt file2.txt 8 > tmp.txt
end="$(date -u +%s.%N)"
elapsed="$(bc <<<"$end-$start")"
echo "Total of $elapsed seconds elapsed for process"
diff tmp.txt file_out.txt
