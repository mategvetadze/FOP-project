//go:build gcd

package main

import "fmt"

func main() {
	var a int
	var b int
	fmt.Scan(&a)
	fmt.Scan(&b)
	var gcd int
	for i := 1; i <= a; i++ {
		if a%i == 0 && b%i == 0 {
			gcd = i
		}
		if a%i == 0 {
			if b%i == 0 {
				gcd = i
			}
		}
	}
	fmt.Println(gcd)
}
