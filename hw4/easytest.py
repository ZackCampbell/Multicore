with open('inp.txt', 'r') as f:
    c = f.read()
    nums = c.split(', ')
    nums = list(map(int, nums))
    print(nums)
    nums.sort()
    print(nums[0])