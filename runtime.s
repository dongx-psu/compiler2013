malloc:
	# a0 -- size in bytes (already x4)
	li $v0, 9
	syscall
	jr $ra
memcpy:
	addiu $sp, $sp, -124
	sw $ra 120($sp)
	sw $t6, 68($sp)
	sw $t5, 64($sp)
	sw $t4, 60($sp)
	sw $t3, 56($sp)
	sw $t2, 52($sp)
	sw $t1, 48($sp)
	sw $t0, 44($sp)
	move $t1, $a0
	move $t2, $a1
	move $t3, $a2
	move $t0, $zero
memcpy_loop:
	sra $t4, $t3, 2 #SR
	bge $t0, $t4, memcpy_loop_over
	sll $t5, $t0, 2 #SR
	addu $t4, $t2, $t5
	lw $t6, 0($t4)
	sll $t5, $t0, 2 #SR
	addu $t4, $t1, $t5
	sw $t6, 0($t4)
	addiu $t0, $t0, 1 #I
	j memcpy_loop
memcpy_loop_over:
	lw $t6, 68($sp)
	lw $t5, 64($sp)
	lw $t4, 60($sp)
	lw $t3, 56($sp)
	lw $t2, 52($sp)
	lw $t1, 48($sp)
	lw $t0, 44($sp)
	lw $ra, 120($sp)
	addiu $sp, $sp, 124
	jr $ra
strcpy:
	addiu $sp, $sp, -120
	sw $ra 116($sp)
	sw $t6, 64($sp)
	sw $t5, 60($sp)
	sw $t4, 56($sp)
	sw $t3, 52($sp)
	sw $t2, 48($sp)
	sw $t1, 44($sp)
	sw $t0, 40($sp)
	move $t1, $a0
	move $t2, $a1
	move $t3, $a2
	li $t0, 0
strcpy_loop:
	bge $t0, $t3, strcpy_loop_over
	move $t4, $t0 #SR
	addu $t5, $t2, $t4
	lb $t6, 0($t5)
	move $t4, $t0 #SR
	addu $t5, $t1, $t4
	sb $t6, 0($t5)
	addiu $t0, $t0, 1 #I
	j strcpy_loop
strcpy_loop_over:
	lw $t6, 64($sp)
	lw $t5, 60($sp)
	lw $t4, 56($sp)
	lw $t3, 52($sp)
	lw $t2, 48($sp)
	lw $t1, 44($sp)
	lw $t0, 40($sp)
	lw $ra, 116($sp)
	addiu $sp, $sp, 120
	jr $ra
strlen:
	addiu $sp, $sp, -104
	sw $ra 100($sp)
	sw $t4, 40($sp)
	sw $t3, 36($sp)
	sw $t2, 32($sp)
	sw $t1, 28($sp)
	sw $t0, 24($sp)
	move $t1, $a0
	li $t0, 0
strlen_loop:
	move $t2, $t0 #SR
	addu $t3, $t1, $t2
	lb $t4, 0($t3)
	beqz $t4, strlen_loop_over
	addiu $t0, $t0, 1 #I
	j strlen_loop
strlen_loop_over:
	move $v0, $t0
	lw $t4, 40($sp)
	lw $t3, 36($sp)
	lw $t2, 32($sp)
	lw $t1, 28($sp)
	lw $t0, 24($sp)
	lw $ra, 100($sp)
	addiu $sp, $sp, 104
	jr $ra
prints:
	# a0 -- str
	li $v0, 4
	syscall
	jr $ra
printd:
	# a0 -- num
	li $v0, 1
	syscall
	jr $ra
printc:
	# a0 -- char
	la $fp, printf_buf
	sb $a0, 0($fp)
	sb $0, 1($fp)
	move $a0, $fp
	li $v0, 4
	syscall
	jr $ra

.data
printf_buf: .space 2