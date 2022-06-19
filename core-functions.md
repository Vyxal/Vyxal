1. Map Over Prefixes - ¡ - Modifier
2. Conditional Execution - ¿ - Modifier
3. Filter by Element - © - Modifier
4. Last Element as Function - ¤ - Modifier
5. Last Two Elements as Function - ¢ - Modifier
6. Last Three Elements as Function - € - Modifier
7. Last Four Elements as Function - § - Modifier
8. Reject by Element - ® - Modifier
9. Outer Product / Table - ┬ - Modifier
10. Normal Lambda - λ - Syntax
11. Mapping Lambda - ƛ - Syntax
12. Filtering Lambda - Ω - Syntax
13. Reduction Lambda - Λ - Syntax
14. Sorting Lambda - µ - Syntax
15. Halve - ½ - Element
16. Range `[1, n]` - ʒ - Element
17. Range `[0, n)` - ʑ - Element
18. Range `[0, n]` - z - Element
19. Multiplication - × - Element
20. Division - ÷ - Element
21. Factorial - ! - Element
22. If statement - [...} - Syntax
23. For loop - (...} - Syntax
24. While loop - {...} - Syntax
25. Strings - "..." - Syntax
26. Numeric literals - [0-9]+ - Syntax
27. String compression - ▲ - Syntax
28. Sort by element - $ - Modifier
29. Lambda to start of line - ) - Modifier
30. Vectorise element - v - Modifier
31. Exponentiation - * - Element
32. Addition - + - Element
33. Subtraction - - - Element
34. Print - , - Element
35. Foldl / Reduce left - / - Modifier
36. Duplicate - : - Element
37. Less than - < - Element
38. Equals - = - Element
39. Greater than - > - Element
40. Input - ? - Element
41. `all()` in python - A (lst) - Element
42. From binary - B (lst | str) - Element
43. To binary - b (num) - Element
44. `a.count(b)` - C - Element
45. Triplicate - D - Element
46. Filter by function - F (any, fun) - Element
47. Map function - M (any, fun) - Element
48. Monadic maximum - G - Element
49. Dyadic maximum - G - Element
50. Monadic minimum - g - Element
51. Dyadic mimimum - g - Element
52. To hexadecimal - H (num) - Element
53. From hexadecimal - H (str) - Element
54. `abs(x) <= 1` - I (num) - Element
55. Concatenate - J (scl, scl) + (str, [num, str]) - Element
56. Negated filter - K (any, fun) - Element
57. Set difference 
58. Length
59. Map function over list
60. Negate
61. `chr/ord`
62. Prepend
63. Reduce by function
64. Transpose matrix/list
65. Transpose matrix/list with filler
66. Uniquify
67. Replace b in a with c
68. Wrap stack
69. Wrap item in list
70. Break / Return
71. Recurse
72. Interleave
73. Uninterleave
74. Zip
75. Monadic mimimum without popping
76. Monadic maximum without popping
77. Reduce each overlap of size 2 by an element
78. Apply at indices
79. scanl
80. scanr
81. foldr
82. counter variable
83. register
84. reverse
85. sum
86. vectorised sums
87. absolute difference
88. sowerset
89. permutations
90. mirror
91. palindromise
92. increment
93. decrement
94. double / dyadify
95. half
96. Floor division
97. Floor
98. Ceiling
99.  Join on newlines
100. Set register
101. Get register
102. Group consecutive
103. Pop
104. Swap
105. Print without newline
106. Print without popping
107. Uppercase
108. Lowercase
109. Rotate left
110. Rotate right
111. Shallow flat
112. Deep flat
113. Head
114. Tail
115. Head extract
116. Tail extract
117. Overlapping chunks of length
118. Generate infinite list from function
119. Cumulative sums
120. Vectorising length
121. Truthy indices
122. Range between two numbers 
123. Range of length of value
124. scan fixedpoint
125. all neighbours
126. truthy indices after applying
127. apply to neighbours
128. invariant / Equal under element
129. all equal
130. increments / deltas
131. join on spaces
132. vectorised reverse
133. choose random item
134. bitwise not
135. bitwise and
136. bitwise or
137. bitwise xor
138. logical not
139. logical and 
140. logical or
141. sort ascending
142. sort descending
143. is prime?
144. is even?
145. parity
146. to string
147. split string on anything
148. split string on newlines
149. split string on spaces
150. to number
151. reciprocal
152. sign of number
153. sublists
154. pair
155. triplet (x, y, z -> [x, y, z])
156. base conversion
157. n choose k
158. n pick k
159. GCD
160. LCM
161. logarithm
162. integer partitions
163. list partitions
164. partition at truthy indices
165. cartesian product
166. cartesian power
167. split into slices of length
168. trim / Trim all elements of y from both sides of x.
169. eval as vyxal
170. find needle in haystack
171. find all needles in haystack
172. dyadic zip
173. absolute value
174. divides?
175. index
176. Order, multiplicity, valuation; how many times is x divisible by y?
177. append
178. dot product
179. shift left
180. shift right
181. repeat x times (string - `[4], 5 -> [4, 4, 4, 4]`)
182. repeat x times (list - `[4], 5 -> [[4], [4], [4], [4]]`)
183. square root
184. sequence generation
185. does there exist an item in container such that predicate returns true
186. rotate stack left
187. rotate stack right
188. rotate top three items
189. 2dup
190. 2nip
191. 2drop
192. dupd
193. over
194. pick ( x y z -- x y z x )
195. swapd ( x y z -- y x z )
196. is string?
197. is number?
198. is list?
199. is positive?
200. is alphabet?
201. is numeric?
202. prime factors
203. prime factorisation
204. 2 ** n
205. 10 ** n
206. lift
207. if 1, push context variable n
208. ath prime
209. e ** n
210. cycled list - [1, 2, 3] -> [1, 2, 3, 1, 2, 3, 1, 2, 3, ...]
211. keep only alphabet
212. keep only numbers
213. cartesian product with self
214. round
215. sort by length
216. join on anything
217. n-dup
218. uppercase
219. lowercase
220. title case
221. first number where function is truthy
222. split on function results
223. run func on the prev result until the result no longer changes returning all intermediate results
224. find the index for the first element such that function evaluates as truthy
225. apply func to the elements in a where the index in b is truthy
226. zipwith
227. Key. Map an element over the groups formed by identical items.
228. Tie. Cycle through a number of (default 2) elements each time called.
229. Keep items with minimal element value
230. Keep items with maximal element value
231. Loop an element. Repeat until the results are no longer unique.
232. Loop an element. Repeat until the results are no longer unique. Record intermediate results.
233. Return a Boolean array with 1s at the indices in a list.
234. vectorised length
235. Shortcut for ĠvL (group consecutive, vectorised lengths)
236. Shape of list
237. Matrix inverse
238. Grade up
239. Grade down
240. Contains - shallow
241. Contains - deep
242. Reshape array
243. Mold one list to the shape of another
244. take (↑ in APL)
245. drop (↓ in APL)
246. Insert zeros (or blanks) in B corresponding to zeros in A
247. Conjugate a complex number
248. Solo (≍ in BQN)
249. Couple (≍ in BQN)
250. Window (↕ in BQN)
251. Vectorise but to the rows (˘ in BQN - cells)
252. Vertical fold (˝ in BQN)
253. Depth of list
254. Vertical scan
255. Deshape (⥊ in BQN)
256. apply last element to each item, treating the item as a stack: ƛ~<element>}
257. maximum by element
258. mimimum by element
259. first truthy item by element
260. remove duplicates by element
261. number of truthy items by element
262. diagonals of list
263. anti-diagonals
264. slice index
265. get function arity
266. remove an item from a list
267. remove a string from another string
268. remove whitespace
269. remove non-numbers
270. remove non-alphabet
271. remove non-alphanumeric
272. remove alphanumeri
273. remove alphabet
274. remove numbers
275. Prepend a default value of the appropriate type to a list. Default values are either falsy values (0,[],...) or functions returning those values.
276. length is 1?
277. apply twice
278. every nth item of a list
279. factors of a number
280. prefixes
281. suffixes
282. Replicate each item a list by a given number
283. Symmetric range
284. are all function results equal?
285. Remove duplicates by function result
286. empty list
287. regex search (`re.search`)
288. regex match (`re.match`)
289. regex fullmatch (`re.fullmatch`)
290. regex split (`re.split`)
291. regex findall (`re.findall`)
292. regex substitute (`re.sub`)
293. regex escape (`re.escape`)
294. overwrite the start of a with b
295. transliteration
296. set equality
297. a.split_before(b) (https://chat.stackexchange.com/transcript/message/58361993#58361993)
298. range from a to b with step c
299. shape a as an a * c rectangle
300. pad a on the right with a prefix of repeated copies of b to a length of the nearest multiple of c
301. canvas
302. a formatted into rectangle with smallest perimeter
303. a and b joined on longest common prefix and suffix
304. remove nth item of a list
305. remove nth letter of a string
306. set union
307. set xor
308. multiset union
309. multiset intersection
310. multiset xor
311. [[a, item] for item in b]
312. reverse stack
313. two things have same length?
314. non-vectorising non-equals
315. exactly equals
316. all and any
317. fold fixedpoint
318. connected uniquify (`Ġvh`)
319. triple / triadify
320. number compression
321. next two bytes number compression
322. powerset
323. empty string
324. empty space
325. move element to beginning of list
326. degrees to radians
327. radians to degrees
328. Complement; compute 1 − z.
329. Return all indices of z that correspond to maximal elements.
330. Return a Boolean array with 1s at the indices in z.
331. enumerate a list
332. Modular; return every y th element of x. If y is zero, mirror: prepend x to its reverse.
333. ring translate
334. Sublist exists; return 1 if x is a contiguous sublist of y, else 0.
335. All permutations of z. May contain duplicates.
336. Apply element only to the head of list
337. Apply element only to the first n elements of list
338. Cartesian product over a list of lists
339. Uniquify Mask
340. is sorted?
341. Pipe ("|") constant
342. Backslash ("\") constant
343. if-equal structure (`=[`)
344. split into lengths of 2 (`2ẇ`)
345. Apply element without popping
346. Parallel apply
347. Parallel apply into list
348. Inner-product by element
349. Apply last element to register
350. Head remove
351. Tail remove