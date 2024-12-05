---
layout: post
title: Advent of Rust Day 4
date: 2024-12-04
---

In Day 4 we need to look at the characters in a grid and count up the number of occurrences of a
word (`XMAS`) in "word search" style (can go any direction including diagonals and backwards).

Initially, I thought it would be fun to create my own custom iterator that was given a position in
the grid and delta x and y values and which yielded each character in that direction until we ran
out of grid. The grid of characters is a `Vec<Vec<char>>`, so the iterator looked like this:

```rust
struct Slice<'a> {
    grid: &'a Vec<Vec<char>>,
    x: isize,
    y: isize,
    dx: isize,
    dy: isize,
}

impl<'a> Iterator for Slice<'a> {
    type Item = char;
    fn next(&mut self) -> Option<Self::Item> {
        let x = self.x as isize;
        let y = self.y as isize;
        if x < 0 || y < 0 || x >= self.grid[0].len() as isize || y >= self.grid.len() as isize {
            None
        } else {
            self.x += self.dx;
            self.y += self.dy;
            Some(self.grid[y as usize][x as usize])
        }
    }
}
```

and then using it look like this:

```rust
let slice = Slice { grid: &grid, x: x as isize, y: y as isize, dx, dy };
slice.take(4).collect::<String>() == "XMAS"
```

Neat, but rather a lot of code just so that I can call `take(4)` and `collect()` to yoink a word
out of the grid at any angle and position.

Anyhow, I didn't worry too much about that, and set about actually checking all the positions and
directions for the word. Since I was programming in a low-level system language, it felt
appropriate to use a bunch of nested for loops:

```rust
pub fn part1(input: &str) -> String {
    let grid: Vec<Vec<char>> = input.lines().map(|line| line.chars().collect()).collect();
    let mut count = 0;
    for y in 0..grid.len() {
        for x in 0..grid[0].len() {
            for dx in -1..=1 {
                for dy in -1..=1 {
                    if dx != 0 || dy != 0 {
                        let slice = Slice { grid: &grid, x: x as isize, y: y as isize, dx, dy };
                        if slice.take(4).collect::<String>() == "XMAS" {
                            count += 1;
                        }
                    }
                }
            }
        }
    }
    count.to_string()
}
```

But I felt a bit like a cave man doing all this manual iteration and then... incrementing a mutable
variable in the inner-most loop. Rust supports functional programming, let's do this with `map` and
`filter` like God and John McCarthy intended:

```rust
pub fn part1(input: &str) -> String {
    let grid: Vec<Vec<char>> = input.lines().map(|line| line.chars().collect()).collect();
    (0..grid.len())
        .flat_map(|y| {
            (0..grid[y].len()).flat_map(move |x| {
                (-1..=1).flat_map(move |dx| (-1..=1).map(move |dy| (x, y, dx, dy)))
            })
        })
        .filter(|&(x, y, dx, dy)| (dx != 0 || dy != 0))
        .filter(|&(x, y, dx, dy)| {
            let slice = Slice { grid: &grid, x: x as isize, y: y as isize, dx, dy };
            slice.take(4).collect::<String>() == "XMAS"
        })
        .count()
        .to_string()
}
```

Well, I can't say that's an improvement. Clearly we're going to need to find some compromise here.
Before we move on to that, I want to shout out to Scala, which has a million problems of its own,
but includes a very nice syntax for this sort of situation. We can use a _for comprehension_ to
enumerate all the positions and directions like so:

```scala
val coords = for (
  y <- 0 until grid.length ; x <- 0 until grid(y).length ; dx <- -1 to 1 ; dy <- -1 to 1
  if (dx != 0 || dy != 0)
) yield (x, y, dx, dy)
```

Then we would filter the coords that match XMAS and count them up. This desugars to effectively the
exact same code that we're doing manually in Rust (modulo the thing I mentioned last time vis a vis
iterables versus iterators), but I think anyone would agree that the ratio of meaningful characters
to line noise is higher in the Scala code.

Anyhow, back to our search for compromise. I didn't love generating the deltas with ranges and
flat_maps because there are only 8 of them, so I just declared them directly. At first this looked
like this, which did not exactly make my heart sing:

```rust
    let grid: Vec<Vec<char>> = input.lines().map(|line| line.chars().collect()).collect();
    let (wid, hei) = (grid[0].len(), grid.len())
    let deltas = [
        (-1, -1),
        (-1, 0),
        (-1, 1),
        (0, -1),
        (0, 1),
        (1, -1),
        (1, 0),
        (1, 1),
    ];
    (0..hei)
        .flat_map(move |y| (0..wid).map(move |x| (x, y)))
        .flat_map(|(x, y)| {
            deltas
                .iter()
                .map(move |&(dx, dy)| (x as isize, y as isize, dx, dy))
        })
        .filter(|&(x, y, dx, dy)| {
            // ...
        })
        .count()
        .to_string()
```

But I took `rustfmt` aside for a brief negotation and we eventually came to understand one another
much better. Now we are more graceful dance partners:

```rust
    let grid: Vec<Vec<char>> = input.lines().map(|line| line.chars().collect()).collect();
    let (wid, hei) = (grid[0].len(), grid.len())
    let coords = (0..hei).flat_map(move |y| (0..wid).map(move |x| (x, y)));
    let deltas = [(-1, -1), (-1, 0), (-1, 1), (0, -1), (0, 1), (1, -1), (1, 0), (1, 1)];
    coords
        .flat_map(|(x, y)| deltas.iter().map(move |&(dx, dy)| (x as isize, y as isize, dx, dy)))
        .filter(|&(x, y, dx, dy)| {
            // ...
        })
        .count()
        .to_string()
```

Is it still line-noisy? A little. Maybe I should name another intermediate variable or two. But at
least I'm not manually looping and incrementing a variable.

In the process of all this golfing, I also started to lose enthusiasm for my custom iterator. It
turned out not to be useful in part two either, so I tried just doing things manually:

```rust
fn check_xmas(grid: &Vec<Vec<char>>, x: usize, y: usize, dx: isize, dy: isize) -> bool {
    let mut x = x as isize;
    let mut y = y as isize;
    let mut chars = "XMAS".chars();
    for _ in 0..4 {
        if x < 0 || y < 0 || x >= grid[0].len() as isize || y >= grid.len() as isize {
            return false;
        }
        if grid[y as usize][x as usize] != chars.next().unwrap() {
            return false;
        }
        x += dx;
        y += dy;
    }
    true
}
```

But now we're back to incrementing mutable variables like a barbarian. There had to be a better
way. Eventually I decided this was way too general purpose. Loops are for suckers, let's just hard
code it.

After factoring out a bit that was also needed for part two, I ended up with:

```rust
fn parse(input: &str) -> (usize, usize, Vec<Vec<char>>) {
    let grid: Vec<Vec<char>> = input.lines().map(|line| line.chars().collect()).collect();
    (grid[0].len(), grid.len(), grid)
}

pub fn part1(input: &str) -> String {
    let (wid, hei, grid) = parse(input);
    let in_grid = |x, y| x >= 0 && y >= 0 && x < wid as isize && y < hei as isize;
    let check = |(x, y, c)| in_grid(x, y) && grid[y as usize][x as usize] == c;
    let coords = (0..hei).flat_map(move |y| (0..wid).map(move |x| (x, y)));
    let deltas = [(-1, -1), (-1, 0), (-1, 1), (0, -1), (0, 1), (1, -1), (1, 0), (1, 1)];
    coords
        .flat_map(|(x, y)| deltas.iter().map(move |&(dx, dy)| (x as isize, y as isize, dx, dy)))
        .filter(|&(x, y, dx, dy)| {
            check((x, y, 'X'))
                && check((x + 1 * dx, y + 1 * dy, 'M'))
                && check((x + 2 * dx, y + 2 * dy, 'A'))
                && check((x + 3 * dx, y + 3 * dy, 'S'))
        })
        .count()
        .to_string()
}
```

Coincidentally, this final solution is the same number of lines (22) as the iterator I wrote at the
beginning.

Though my part two solution evolved somewhat during this code golfing process, it was never as
troublesome as part one. Enumerating where to do the checks is a mere two lines:

```rust
pub fn part2(input: &str) -> String {
    let (wid, hei, grid) = parse(input);
    let check_x_mas = |&(x, y): &(usize, usize)| {
        let ul = grid[y - 1][x - 1];
        let ur = grid[y - 1][x + 1];
        let ll = grid[y + 1][x - 1];
        let lr = grid[y + 1][x + 1];
        grid[y][x] == 'A'
            && ((ul == 'M' && lr == 'S') || (ul == 'S' && lr == 'M'))
            && ((ur == 'M' && ll == 'S') || (ur == 'S' && ll == 'M'))
    };
    let coords = (1..hei - 1).flat_map(move |y| (1..wid - 1).map(move |x| (x, y)));
    coords.filter(check_x_mas).count().to_string()
}
```

I'm not sure how much energy I'll have for making my code dense, functional, and hard to understand
when we get into the challenging late-teens and twenties. But I enjoyed this little round of golf.
I've also probably ensured that no one will ever hire me to write Rust code.
