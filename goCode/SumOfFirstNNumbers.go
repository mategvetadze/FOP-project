//go:build sum

package main

import "fmt"

func main() {
	var n int
	fmt.Scan(&n)
	var sum int
	for i := 1; i <= n; i++ {
		sum = sum + i
	}
	fmt.Println(sum)
}