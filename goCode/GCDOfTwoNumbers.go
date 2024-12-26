//go:build gcd

package main

import "fmt"

func main() {
	var a int
	var b int
	fmt.Scan(&a)
	fmt.Scan(&b)
	var gcd int
	for a>=1 && b>=1 {
		if a>b {
		    a = a%b
        } else {
            b = b%a
        }

	}
    if a==0 {
        gcd = b
    } else {
        gcd = a
    }
	fmt.Println(gcd)
}
