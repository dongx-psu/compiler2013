########################################
############### CODE GEN ###############
########################################
	.data
	.align 2
	.globl args
args:	.space 4
	.align 2
	.text
	.align 2
	.globl main
main:
	la $v1, args
	jal run_main
	li $v0, 10
	syscall
	jr $ra
run_main:
	addiu $sp, $sp, -228
	sw $ra 224($sp)
	sw $s3, 192($sp)
	sw $s2, 188($sp)
	sw $s1, 184($sp)
	sw $s0, 180($sp)
	sw $t7, 176($sp)
	sw $t6, 172($sp)
	sw $t5, 168($sp)
	sw $t4, 164($sp)
	sw $t3, 160($sp)
	sw $t2, 156($sp)
	sw $t1, 152($sp)
	sw $t0, 148($sp)
	li $t6, 36
	move $a0, $t6
	jal malloc
	move $t5, $v0
	li $t7, 36
	move $a0, $t7
	jal malloc
	move $t3, $v0
	li $t6, 36
	move $a0, $t6
	jal malloc
	move $t1, $v0
	li $t2, 0
L0:
	bge $t2, 3, L1
	li $t4, 0
L2:
	bge $t4, 3, L3
	li $t0, 0
L4:
	bge $t0, 3, L5
	mul $t6, $t2, 12 #I
	addu $t7, $t1, $t6
	sll $s0, $t0, 2 #SR
	addu $t6, $t7, $s0
	lw $s1, 0($t6)
	mul $t7, $t2, 12 #I
	addu $t6, $t5, $t7
	sll $s0, $t4, 2 #SR
	addu $t7, $t6, $s0
	lw $s2, 0($t7)
	mul $t6, $t4, 12 #I
	addu $t7, $t3, $t6
	sll $s0, $t0, 2 #SR
	addu $t6, $t7, $s0
	lw $s3, 0($t6)
	mul $t7, $s2, $s3
	addu $t6, $s1, $t7
	mul $s0, $t2, 12 #I
	addu $t7, $t1, $s0
	sll $s1, $t0, 2 #SR
	addu $s0, $t7, $s1
	sw $t6, 0($s0)
	addiu $t0, $t0, 1 #I
	j L4
L5:
	addiu $t4, $t4, 1 #I
	j L2
L3:
	addiu $t2, $t2, 1 #I
	j L0
L1:
	li $t0, 0
	move $v0, $t0
	j run_main_leave
run_main_leave:
	lw $s3, 192($sp)
	lw $s2, 188($sp)
	lw $s1, 184($sp)
	lw $s0, 180($sp)
	lw $t7, 176($sp)
	lw $t6, 172($sp)
	lw $t5, 168($sp)
	lw $t4, 164($sp)
	lw $t3, 160($sp)
	lw $t2, 156($sp)
	lw $t1, 152($sp)
	lw $t0, 148($sp)
	lw $ra, 224($sp)
	addiu $sp, $sp, 228
	jr $ra
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
