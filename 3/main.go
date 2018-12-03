package main

import (
	"fmt"
	"io/ioutil"
	"regexp"
	"strconv"
	"strings"
)

type Coordinate struct {
	x, y int
}

type Frequency struct {
	n int
}

func readFile(filename string) string {
	data, _ := ioutil.ReadFile(filename)
	return string(data)
}

func writeClaim(id, x, y, w, h int, claims map[Coordinate]*Frequency, claimsById map[int][]*Frequency) {
	for i := 0; i < w; i++ {
		for j := 0; j < h; j++ {
			coordinate := Coordinate{x + i, y + j}
			
			if frequency, ok := claims[coordinate]; ok {
				frequency.n++
			} else {
				frequency := Frequency{1}
				claims[coordinate] = &frequency
			}

			claimsById[id] = append(claimsById[id], claims[coordinate])
		}
	}
}

func parseClaim(claim string) (id, x, y, w, h int) {
	r, _ := regexp.Compile("\\#(\\d+)\\s\\@\\s(\\d+),(\\d+)\\:\\s(\\d+)x(\\d+)")
	matches := r.FindStringSubmatch(claim)
	id, _ = strconv.Atoi(matches[1])
	x, _ = strconv.Atoi(matches[2])
	y, _ = strconv.Atoi(matches[3])
	w, _ = strconv.Atoi(matches[4])
	h, _ = strconv.Atoi(matches[5])
	return
}

func main() {
	data := strings.Split(readFile("input.txt"), "\n")
	claims := make(map[Coordinate]*Frequency)
	claimsById := make(map[int][]*Frequency)

	for _, line := range data {
		id, x, y, w, h := parseClaim(line)
		writeClaim(id, x, y, w, h, claims, claimsById)
	}

	count := 0

	for _, v := range claims {
		if v.n >= 2 {
			count++
		}
	}


	for k,v := range claimsById {
		overlap := false

		for _, frequency := range v {
			if frequency.n != 1 {
				overlap = true
			}
		}

		if !overlap {
			fmt.Println(k)
		}
	}

	fmt.Println(count)
}
